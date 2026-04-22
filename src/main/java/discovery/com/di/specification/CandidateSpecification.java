package discovery.com.di.specification;

import discovery.com.di.model.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.List;

public class CandidateSpecification {

    /**
     * Full-text search across name, role title, and skills.
     * Uses a subquery for skill matching to avoid duplicate rows in pagination.
     */
    public static Specification<Candidate> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String like = "%" + search.toLowerCase() + "%";

            // Subquery: does this candidate have at least one matching skill?
            Subquery<Integer> skillSubquery = query.subquery(Integer.class);
            Root<CandidateSkill> skillRoot = skillSubquery.from(CandidateSkill.class);
            skillSubquery.select(cb.literal(1))
                    .where(
                            cb.equal(skillRoot.get("candidate"), root),
                            cb.like(cb.lower(skillRoot.get("skill")), like)
                    );

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("role")), like),
                    cb.exists(skillSubquery)
            );
        };
    }

    /** Filter by exact subcategory/role id. */
    public static Specification<Candidate> hasRole(String roleId) {
        return (root, query, cb) ->
                (roleId == null || roleId.isBlank()) ? null :
                        cb.equal(root.get("roleEntity").get("id"), roleId);
    }

    /**
     * Filter candidates whose roleEntity belongs to the given category.
     * roleIds = all subcategory IDs for that category.
     */
    public static Specification<Candidate> hasCategory(List<String> roleIds) {
        return (root, query, cb) -> {
            if (roleIds == null || roleIds.isEmpty()) {
                return cb.disjunction(); // always false → 0 results
            }
            return root.get("roleEntity").get("id").in(roleIds);
        };
    }

    /** Filter by location (case-insensitive partial match). */
    public static Specification<Candidate> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) return null;
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    /** Filter by minimum experience (years). */
    public static Specification<Candidate> hasMinExperience(Integer minExp) {
        return (root, query, cb) ->
                (minExp == null) ? null : cb.greaterThanOrEqualTo(root.get("experience"), minExp);
    }

    /** Filter by maximum experience (years). */
    public static Specification<Candidate> hasMaxExperience(Integer maxExp) {
        return (root, query, cb) ->
                (maxExp == null) ? null : cb.lessThanOrEqualTo(root.get("experience"), maxExp);
    }

    /** Filter by maximum starting price. */
    public static Specification<Candidate> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                (maxPrice == null) ? null : cb.lessThanOrEqualTo(root.get("priceFrom"), maxPrice);
    }
}
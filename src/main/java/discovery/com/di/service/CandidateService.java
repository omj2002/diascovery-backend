package discovery.com.di.service;

import discovery.com.di.model.*;
import discovery.com.di.repository.*;
import discovery.com.di.specification.CandidateSpecification;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepo;
    private final SubCategoryRepository subRepo;

    public Page<Candidate> getCandidates(
            String search,
            String categoryId,
            String roleId,
            String location,
            Integer minExp,
            Integer maxExp,
            Double maxPrice,
            String sort,
            Pageable pageable) {

        Specification<Candidate> spec = Specification.where(null);

        // Search filter (name / role title / skills)
        spec = spec.and(CandidateSpecification.search(search));

        // Subcategory (role) filter
        if (roleId != null && !roleId.isBlank())
            spec = spec.and(CandidateSpecification.hasRole(roleId));

        // Category filter — look up all subcategory IDs for this category
        if (categoryId != null && !categoryId.isBlank()) {
            List<String> roleIds = subRepo.findByCategory_Id(categoryId)
                    .stream()
                    .map(SubCategory::getId)
                    .toList();
            spec = spec.and(CandidateSpecification.hasCategory(roleIds));
        }

        // Location filter
        spec = spec.and(CandidateSpecification.hasLocation(location));

        // Experience range filter
        spec = spec.and(CandidateSpecification.hasMinExperience(minExp));
        spec = spec.and(CandidateSpecification.hasMaxExperience(maxExp));

        // Max price filter
        spec = spec.and(CandidateSpecification.hasMaxPrice(maxPrice));

        // Sorting
        Sort sorting = Sort.by("rating").descending();
        if ("experience".equals(sort))
            sorting = Sort.by("experience").descending();
        else if ("price".equals(sort))
            sorting = Sort.by("priceFrom").ascending();

        return candidateRepo.findAll(
                spec,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting)
        );
    }

    public List<Candidate> getTopRated() {
        return candidateRepo.findAll(
                PageRequest.of(0, 8, Sort.by("rating").descending())
        ).getContent();
    }

    public Candidate getById(String id) {
        return candidateRepo.findById(id).orElseThrow();
    }
}
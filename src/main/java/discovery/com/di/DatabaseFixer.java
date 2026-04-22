package discovery.com.di;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseFixer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixDatabase() {

        // 1. Fix profile_image column size
        try {
            jdbcTemplate.execute("ALTER TABLE candidate MODIFY COLUMN profile_image LONGTEXT");
            System.out.println("✅ profile_image column set to LONGTEXT.");
        } catch (Exception e) {
            System.out.println("ℹ️ profile_image column already LONGTEXT or failed: " + e.getMessage());
        }

        // 2. Cleanup orphaned seller users
        try {
            jdbcTemplate.execute(
                "DELETE FROM users WHERE role = 'seller' AND id NOT IN (SELECT user_id FROM candidate)");
            System.out.println("🧹 Cleaned up orphan seller users.");
        } catch (Exception e) {
            System.out.println("ℹ️ Orphan user cleanup skipped: " + e.getMessage());
        }

        // 3. *** FIX: Re-assign each candidate's role_id based on their role title ***
        //    This handles all the mismatched data — Cricketers in Digital & Software, etc.
        try {
            int fixed = jdbcTemplate.update(
                "UPDATE candidate c " +
                "INNER JOIN sub_category sc ON LOWER(TRIM(c.role)) = LOWER(TRIM(sc.title)) " +
                "SET c.role_id = sc.id " +
                "WHERE c.role_id IS NULL OR c.role_id != sc.id"
            );
            System.out.println("🔧 Fixed " + fixed + " candidate(s) with wrong/missing role_id based on role title match.");
        } catch (Exception e) {
            System.out.println("⚠️ Role-id fix failed: " + e.getMessage());
        }

        // 4. Fix common partial-match cases where role title ≠ exact subcategory title
        //    e.g. "Designer" → "UI/UX Designer", "Italian Chef" → "Chef"
        fixPartialRoleMatches();

        // 5. Print final category tree for verification
        System.out.println("\n========== CATEGORY DEBUG (after fix) ==========");
        try {
            List<Map<String, Object>> cats = jdbcTemplate.queryForList("SELECT id, title FROM category ORDER BY title");
            for (Map<String, Object> cat : cats) {
                List<Map<String, Object>> subs = jdbcTemplate.queryForList(
                    "SELECT id, title FROM sub_category WHERE category_id = ?", cat.get("id"));
                long total = subs.stream().mapToLong(sub -> {
                    List<Map<String, Object>> c = jdbcTemplate.queryForList(
                        "SELECT COUNT(*) as cnt FROM candidate WHERE role_id = ?", sub.get("id"));
                    return ((Number) c.get(0).get("cnt")).longValue();
                }).sum();
                System.out.println("📂 " + cat.get("title") + " → " + total + " candidate(s)");
            }

            List<Map<String, Object>> orphans = jdbcTemplate.queryForList(
                "SELECT name, role FROM candidate WHERE role_id IS NULL " +
                "OR role_id NOT IN (SELECT id FROM sub_category)");
            if (!orphans.isEmpty()) {
                System.out.println("⚠️ Still uncategorized: " + orphans.size() + " candidate(s)");
                for (Map<String, Object> c : orphans) {
                    System.out.println("  ❌ " + c.get("name") + " — role: '" + c.get("role") + "'");
                }
            } else {
                System.out.println("✅ All candidates are correctly categorized!");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Debug query failed: " + e.getMessage());
        }
        System.out.println("=================================================\n");
    }

    private void fixPartialRoleMatches() {
        // Map of candidate role values → correct subcategory ID
        // These are cases where titles don't exactly match
        Object[][] mappings = {
            // role keyword      subcategory id
            {"designer",        "sub7"},   // "Designer" → "UI/UX Designer"
            {"italian chef",    "sub6"},   // "Italian Chef" → "Chef"
            {"cook",            "sub6"},   // "Cook" → "Chef"
            {"software",        "sub1"},   // "Software Engineer" etc → "Java Developer"
            {"singer",          null},     // no matching subcategory, skip
        };

        for (Object[] mapping : mappings) {
            String keyword = (String) mapping[0];
            String subId   = (String) mapping[1];
            if (subId == null) continue;
            try {
                // No guard on current role_id — always move to the correct subcategory
                // This fixes candidates who were wrongly placed in another valid category (e.g. Designer in Culinary)
                int n = jdbcTemplate.update(
                    "UPDATE candidate SET role_id = ? WHERE LOWER(role) LIKE ?",
                    subId, "%" + keyword + "%"
                );
                if (n > 0) System.out.println("🔧 Partial fix: " + n + " '" + keyword + "' candidate(s) → " + subId);
            } catch (Exception e) {
                System.out.println("⚠️ Partial fix failed for '" + keyword + "': " + e.getMessage());
            }
        }
    }
}

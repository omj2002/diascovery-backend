package discovery.com.di;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class DbChecker {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void dropForeignKeys() {
        try {
            System.out.println("Dropping foreign key fk_seller...");
            jdbcTemplate.execute("ALTER TABLE conversations DROP FOREIGN KEY fk_seller;");
            System.out.println("Dropped fk_seller successfully.");
            
            System.out.println("Dropping foreign key fk_buyer...");
            jdbcTemplate.execute("ALTER TABLE conversations DROP FOREIGN KEY fk_buyer;");
            System.out.println("Dropped fk_buyer successfully.");
        } catch (Exception e) {
            System.out.println("Could not drop constraints, maybe they are already dropped: " + e.getMessage());
        }
    }
}

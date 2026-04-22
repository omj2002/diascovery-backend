package discovery.com.di.repository;

import discovery.com.di.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    List<SubCategory> findByCategory_Id(String categoryId);
}
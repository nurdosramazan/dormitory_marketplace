package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Category;
import nu.senior_project.dormitory_marketplace.entity.image.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    public CategoryImage findCategoryImageByCategory(Category category);
    public CategoryImage findCategoryImageByCategoryId(Long categoryId);

    public void deleteCategoryImageById(Long id);
}

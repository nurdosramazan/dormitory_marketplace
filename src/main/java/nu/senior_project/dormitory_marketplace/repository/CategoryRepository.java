package nu.senior_project.dormitory_marketplace.repository;

import jakarta.validation.constraints.NotNull;
import nu.senior_project.dormitory_marketplace.entity.Category;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    default Category findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Category not found Exception"));
    }
}

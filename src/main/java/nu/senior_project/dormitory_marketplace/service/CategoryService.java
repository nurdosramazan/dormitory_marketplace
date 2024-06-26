package nu.senior_project.dormitory_marketplace.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nu.senior_project.dormitory_marketplace.dto.category.CategoryDto;
import nu.senior_project.dormitory_marketplace.entity.Category;
import nu.senior_project.dormitory_marketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityConverterService entityConverterService;

    @Transactional
    public List<CategoryDto> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(entityConverterService::toCategoryDto).toList();
    }

    @Transactional
    public Long createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return (categoryRepository.save(category)).getId();
    }

    public Long updateCategory(Long id, String name){
    	Category category = categoryRepository.findByIdOrThrow(id);
        category.setName(name);

        return categoryRepository.save(category).getId();
    }

    public CategoryDto getCategory(Long id) {
        return entityConverterService.toCategoryDto(categoryRepository.findByIdOrThrow(id));
    }


    public Category getCategoryEntity(Long id) {
        return categoryRepository.findByIdOrThrow(id);
    }

}

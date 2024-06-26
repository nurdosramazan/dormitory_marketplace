package nu.senior_project.dormitory_marketplace.controller;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.category.CategoryDto;
import nu.senior_project.dormitory_marketplace.service.CategoryService;
import nu.senior_project.dormitory_marketplace.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ImageService imageService;
    @GetMapping("/list")
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }
    @PostMapping("/create")
    public Long createCategory(@RequestParam() String name,
                                @RequestParam(name = "image") MultipartFile file) {
        Long id = categoryService.createCategory(name);
        imageService.saveCategoryImage(file, id);
        return id;
    }

    @PutMapping("/update") //works
    public Long updateCategory(@RequestParam() Long id,
                                @RequestParam String name) { //needs to be tested after heroku is back
        return categoryService.updateCategory(id, name);
    }

    @PutMapping("/update-image") //works
    public void updateCategoryImage(@RequestParam() Long id,
                                @RequestParam(name = "image") MultipartFile image) {
        imageService.updateCategoryImage(image, id);
    }
}

package javadev.project.producer.controller;

import javadev.project.producer.dto.category.CategoryDTO;
import javadev.project.producer.dto.category.CategoryRequest;
import javadev.project.producer.dto.category.CategoryResponse;
import javadev.project.producer.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@Tag(name = "Category Management", description = "APIs for managing categories")
@RequestMapping("/api/v1")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    /**
     * Creates a new category
     * Accepts category details in the request body and creates a new category in the system
     * 
     * @param categoryRequest the category data to be created
     * @return CategoryResponse with success message
     */
    @PostMapping(path = "/category", consumes = "application/json", produces = "application/json")
    public CategoryResponse<String> createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        return CategoryResponse.<String>builder()
                .message("Category created successfully")
                .data(null)
                .build();
    }

    /**
     * Retrieves all categories
     * Returns a list of all categories available in the system
     * 
     * @return CategoryResponse containing a list of all categories
     */
    @GetMapping(path = "/categories", produces = "application/json")
    public CategoryResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return CategoryResponse.<List<CategoryDTO>>builder()
                .message("Categories retrieved successfully")
                .data(categories)
                .build();
    }

    /**
     * Retrieves a specific category by ID
     * Returns detailed information about a single category
     * 
     * @param id the ID of the category to retrieve
     * @return CategoryResponse containing the category details
     */
    @GetMapping(path = "/category/{id}", produces = "application/json")
    public CategoryResponse<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return CategoryResponse.<CategoryDTO>builder()
                .message("Category retrieved successfully")
                .data(category)
                .build();
    }

    /**
     * Updates an existing category
     * Accepts updated category details and modifies the category with the given ID
     * 
     * @param id the ID of the category to update
     * @param categoryRequest the updated category data
     * @return CategoryResponse containing the updated category details
     */
    @PutMapping(path = "/category/{id}", consumes = "application/json", produces = "application/json")
    public CategoryResponse<CategoryDTO> updateCategory(
            @PathVariable Integer id,
            @RequestBody CategoryRequest categoryRequest) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return CategoryResponse.<CategoryDTO>builder()
                .message("Category updated successfully")
                .data(updatedCategory)
                .build();
    }

    /**
     * Deletes a category by ID
     * Removes the category with the specified ID from the system
     * Checks if the category is being used by any products before deletion
     * 
     * @param id the ID of the category to delete
     * @return CategoryResponse with success message
     */
    @DeleteMapping(path = "/category/{id}", produces = "application/json")
    public CategoryResponse<String> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return CategoryResponse.<String>builder()
                .message("Category deleted successfully")
                .data(null)
                .build();
    }
}

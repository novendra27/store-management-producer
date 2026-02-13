package javadev.project.producer.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javadev.project.producer.dto.category.CategoryDTO;
import javadev.project.producer.dto.category.CategoryRequest;
import javadev.project.producer.entity.category;
import javadev.project.producer.repository.CategoryRepository;
import javadev.project.producer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Validator validator;

    /**
     * Creates a new category in the system
     * Validates the category request and persists the category to the database
     * 
     * @param categoryRequest the category data to be created
     * @throws ConstraintViolationException if validation fails
     */
    @Transactional
    public void createCategory(CategoryRequest categoryRequest) {
        // Validate the incoming request
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(categoryRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Map CategoryRequest to Category entity
        category category = new category();
        category.setCategoryName(categoryRequest.getCategoryName());
        categoryRepository.save(category);
    }

    /**
     * Retrieves all categories from the database
     * Converts each category entity to CategoryDTO for response
     * 
     * @return List of CategoryDTO containing all categories
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single category by its ID
     * 
     * @param id the ID of the category to retrieve
     * @return CategoryDTO containing the category details
     * @throws ResponseStatusException if category is not found with the given ID
     */
    public CategoryDTO getCategoryById(Integer id) {
        category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        return convertToDTO(category);
    }

    /**
     * Updates an existing category with new data
     * Validates the category request and updates the category
     * 
     * @param id the ID of the category to update
     * @param categoryRequest the new category data
     * @return CategoryDTO containing the updated category details
     * @throws ConstraintViolationException if validation fails
     * @throws ResponseStatusException if category is not found
     */
    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryRequest categoryRequest) {
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(categoryRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + id));

        existingCategory.setCategoryName(categoryRequest.getCategoryName());

        category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    /**
     * Deletes a category from the database by its ID
     * Checks if the category is currently being used by any products
     * If the category is in use, the deletion is prevented
     * 
     * @param id the ID of the category to delete
     * @throws ResponseStatusException if category is not found or is currently in use by products
     */
    @Transactional
    public void deleteCategory(Integer id) {
        category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + id));

        // Check if category is being used by any products
        long productCount = productRepository.countByCategory(existingCategory);
        if (productCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete category. It is currently being used by " + productCount + " product(s)");
        }

        categoryRepository.delete(existingCategory);
    }

    /**
     * Converts a category entity to CategoryDTO for API response
     * 
     * @param category the category entity to convert
     * @return CategoryDTO containing the category data
     */
    private CategoryDTO convertToDTO(category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

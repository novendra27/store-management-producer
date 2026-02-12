package javadev.project.producer.controller;

import javadev.project.producer.dto.product.ProductDTO;
import javadev.project.producer.dto.product.ProductRequest;
import javadev.project.producer.dto.product.ProductResponse;
import javadev.project.producer.service.ProductService;
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
@Tag(name = "Product Management", description = "APIs for managing products")
@RequestMapping("/api/v1")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * Creates a new product
     * Accepts product details in the request body and creates a new product in the
     * system
     * 
     * @param productRequest the product data to be created
     * @return ProductResponse with success message
     */
    @PostMapping(path = "/product", consumes = "application/json", produces = "application/json")
    public ProductResponse<String> createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
        return ProductResponse.<String>builder()
                .message("Product created successfully")
                .data(null)
                .build();
    }

    /**
     * Retrieves all products
     * Returns a list of all products available in the system
     * 
     * @return ProductResponse containing a list of all products
     */
    @GetMapping(path = "/products", produces = "application/json")
    public ProductResponse<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ProductResponse.<List<ProductDTO>>builder()
                .message("Products retrieved successfully")
                .data(products)
                .build();
    }

    /**
     * Retrieves a specific product by ID
     * Returns detailed information about a single product
     * 
     * @param id the ID of the product to retrieve
     * @return ProductResponse containing the product details
     */
    @GetMapping(path = "/product/{id}", produces = "application/json")
    public ProductResponse<ProductDTO> getProductById(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return ProductResponse.<ProductDTO>builder()
                .message("Product retrieved successfully")
                .data(product)
                .build();
    }

    /**
     * Updates an existing product
     * Accepts updated product details and modifies the product with the given ID
     * 
     * @param id             the ID of the product to update
     * @param productRequest the updated product data
     * @return ProductResponse containing the updated product details
     */
    @PutMapping(path = "/product/{id}", consumes = "application/json", produces = "application/json")
    public ProductResponse<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest productRequest) {
        ProductDTO updatedProduct = productService.updateProduct(id, productRequest);
        return ProductResponse.<ProductDTO>builder()
                .message("Product updated successfully")
                .data(updatedProduct)
                .build();
    }

    /**
     * Deletes a product by ID
     * Removes the product with the specified ID from the system
     * 
     * @param id the ID of the product to delete
     * @return ProductResponse with success message
     */
    @DeleteMapping(path = "/product/{id}", produces = "application/json")
    public ProductResponse<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ProductResponse.<String>builder()
                .message("Product deleted successfully")
                .data(null)
                .build();
    }
}

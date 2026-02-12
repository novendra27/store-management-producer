package javadev.project.producer.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javadev.project.producer.dto.product.ProductDTO;
import javadev.project.producer.dto.product.ProductRequest;
import javadev.project.producer.entity.category;
import javadev.project.producer.entity.product;
import javadev.project.producer.entity.stockLog;
import javadev.project.producer.entity.supplier;
import javadev.project.producer.repository.CategoryRepository;
import javadev.project.producer.repository.ProductRepository;
import javadev.project.producer.repository.StockLogRepository;
import javadev.project.producer.repository.SupplierRepository;
import javadev.project.producer.repository.TransactionDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Autowired
    private StockLogRepository stockLogRepository;

    @Autowired
    private Validator validator;

    /**
     * Creates a new product in the system
     * Validates the product request, fetches related category and supplier
     * entities,
     * and persists the product to the database
     * 
     * @param productRequest the product data to be created
     * @throws ConstraintViolationException if validation fails
     * @throws ResponseStatusException      if category or supplier is not found
     */
    @Transactional
    public void createProduct(ProductRequest productRequest) {
        // Validate the incoming request
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Fetch category and supplier entities
        category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + productRequest.getCategoryId()));

        supplier supplier = supplierRepository.findById(productRequest.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + productRequest.getSupplierId()));

        // Map ProductRequest to Product entity
        product product = new product();
        product.setSku(productRequest.getSku());
        product.setProductName(productRequest.getProductName());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setCurrentStock(productRequest.getCurrentStock());
        product.setPrice(productRequest.getPrice());
        product savedProduct = productRepository.save(product);

        // Create stock log entry for PURCHASE
        stockLog stockLog = new stockLog();
        stockLog.setProduct(savedProduct);
        stockLog.setQuantityChange(savedProduct.getCurrentStock());
        stockLog.setLogType("PURCHASE");
        stockLogRepository.save(stockLog);
    }

    /**
     * Retrieves all products from the database
     * Converts each product entity to ProductDTO for response
     * 
     * @return List of ProductDTO containing all products
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single product by its ID
     * 
     * @param id the ID of the product to retrieve
     * @return ProductDTO containing the product details
     * @throws ResponseStatusException if product is not found with the given ID
     */
    public ProductDTO getProductById(Integer id) {
        product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        return convertToDTO(product);
    }

    /**
     * Updates an existing product with new data
     * Validates the product request, fetches related entities, and updates the
     * product
     * 
     * @param id             the ID of the product to update
     * @param productRequest the new product data
     * @return ProductDTO containing the updated product details
     * @throws ConstraintViolationException if validation fails
     * @throws ResponseStatusException      if product, category, or supplier is not
     *                                      found
     */
    @Transactional
    public ProductDTO updateProduct(Integer id, ProductRequest productRequest) {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        // Store old stock value for comparison
        Integer oldStock = existingProduct.getCurrentStock();

        // Fetch category and supplier entities
        category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + productRequest.getCategoryId()));

        supplier supplier = supplierRepository.findById(productRequest.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + productRequest.getSupplierId()));

        existingProduct.setSku(productRequest.getSku());
        existingProduct.setProductName(productRequest.getProductName());
        existingProduct.setCategory(category);
        existingProduct.setSupplier(supplier);
        existingProduct.setCurrentStock(productRequest.getCurrentStock());
        existingProduct.setPrice(productRequest.getPrice());

        product updatedProduct = productRepository.save(existingProduct);

        // Check if current_stock has changed
        if (!oldStock.equals(productRequest.getCurrentStock())) {
            // Calculate stock difference
            Integer quantityChange = productRequest.getCurrentStock() - oldStock;

            // Create stock log entry for ADJUSTMENT
            stockLog stockLog = new stockLog();
            stockLog.setProduct(updatedProduct);
            stockLog.setQuantityChange(quantityChange);
            stockLog.setLogType("ADJUSTMENT");
            stockLogRepository.save(stockLog);
        }

        return convertToDTO(updatedProduct);
    }

    /**
     * Deletes a product from the database by its ID
     * Checks if the product is currently being used in transaction details or stock logs
     * If the product is in use, the deletion is prevented
     * 
     * @param id the ID of the product to delete
     * @throws ResponseStatusException if product is not found or is currently in use
     */
    @Transactional
    public void deleteProduct(Integer id) {
        product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        // Check if product is being used in transaction details
        long transactionDetailCount = transactionDetailRepository.countByProduct(existingProduct);
        if (transactionDetailCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete product. It is currently being used in " + transactionDetailCount + " transaction detail(s)");
        }

        // Check if product is being used in stock logs
        long stockLogCount = stockLogRepository.countByProduct(existingProduct);
        if (stockLogCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete product. It has " + stockLogCount + " stock log record(s)");
        }

        productRepository.delete(existingProduct);
    }

    /**
     * Converts a product entity to ProductDTO for API response
     * 
     * @param product the product entity to convert
     * @return ProductDTO containing the product data
     */
    private ProductDTO convertToDTO(product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .categoryId(product.getCategory().getId())
                .supplierId(product.getSupplier().getId())
                .productName(product.getProductName())
                .currentStock(product.getCurrentStock())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

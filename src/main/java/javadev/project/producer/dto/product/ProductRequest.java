package javadev.project.producer.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be greater than or equal to 0")
    private Integer currentStock;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Price must have maximum 13 integer digits and 2 decimal places")
    private BigDecimal price;
}

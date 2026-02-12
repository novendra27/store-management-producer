package javadev.project.producer.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must not exceed 100 characters")
    private String supplierName;

    @NotBlank(message = "Contact is required")
    @Size(max = 20, message = "Contact must not exceed 20 characters")
    private String contact;

    @NotBlank(message = "Address is required")
    private String address;
}

package javadev.project.producer.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierDTO {
    private Integer id;
    private String supplierName;
    private String contact;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

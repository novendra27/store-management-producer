package javadev.project.producer.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportDTO {
    private Integer no;
    private Integer transactionId;
    private LocalDate transactionDate;
    private String productName;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal totalPrice;
}

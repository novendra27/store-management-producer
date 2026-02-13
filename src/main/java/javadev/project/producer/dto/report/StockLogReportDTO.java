package javadev.project.producer.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockLogReportDTO {
    private Integer no;
    private Integer logId;
    private String productName;
    private Integer qty;
    private String logType;
    private LocalDateTime createdAt;
}

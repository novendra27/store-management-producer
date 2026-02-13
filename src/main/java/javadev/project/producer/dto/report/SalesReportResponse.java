package javadev.project.producer.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportResponse<T> {

    private String message;
    private T data;
    private String errors;
}

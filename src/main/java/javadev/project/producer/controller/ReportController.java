package javadev.project.producer.controller;

import javadev.project.producer.dto.report.SalesReportDTO;
import javadev.project.producer.dto.report.SalesReportResponse;
import javadev.project.producer.dto.report.StockLogReportDTO;
import javadev.project.producer.dto.report.StockLogReportResponse;
import javadev.project.producer.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Report Management", description = "APIs for generating reports")
@RequestMapping("/api/v1")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Generates sales report based on date range
     * Returns detailed sales information including transaction details and product
     * information
     * 
     * @param startDate the start date for filtering transactions (format:
     *                  yyyy-MM-dd)
     * @param endDate   the end date for filtering transactions (format: yyyy-MM-dd)
     * @return SalesReportResponse containing a list of sales report data
     */
    @GetMapping(path = "/report/sales", produces = "application/json")
    public SalesReportResponse<List<SalesReportDTO>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<SalesReportDTO> salesReport = reportService.getSalesReport(startDate, endDate);

        return SalesReportResponse.<List<SalesReportDTO>>builder()
                .message("Sales report retrieved successfully")
                .data(salesReport)
                .build();
    }

    /**
     * Generates stock log report based on date range
     * Returns detailed stock log information including product details and quantity changes
     * 
     * @param startDate the start date for filtering stock logs (format:
     *                  yyyy-MM-dd)
     * @param endDate   the end date for filtering stock logs (format: yyyy-MM-dd)
     * @return StockLogReportResponse containing a list of stock log report data
     */
    @GetMapping(path = "/report/stockLog", produces = "application/json")
    public StockLogReportResponse<List<StockLogReportDTO>> getStockLogReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<StockLogReportDTO> stockLogReport = reportService.getStockLogReport(startDate, endDate);

        return StockLogReportResponse.<List<StockLogReportDTO>>builder()
                .message("Stock log report retrieved successfully")
                .data(stockLogReport)
                .build();
    }
}

package javadev.project.producer.service;

import javadev.project.producer.dto.report.SalesReportDTO;
import javadev.project.producer.dto.report.StockLogReportDTO;
import javadev.project.producer.entity.stockLog;
import javadev.project.producer.entity.transactionDetail;
import javadev.project.producer.entity.transactionHistory;
import javadev.project.producer.repository.StockLogRepository;
import javadev.project.producer.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private StockLogRepository stockLogRepository;

    /**
     * Generates sales report based on transaction date range
     * Retrieves transaction details within the specified date range and formats them for reporting
     * 
     * @param startDate the start date for the report filter (inclusive)
     * @param endDate the end date for the report filter (inclusive)
     * @return List of SalesReportDTO containing sales data
     * @throws ResponseStatusException if start date is after end date or no data found
     */
    public List<SalesReportDTO> getSalesReport(LocalDate startDate, LocalDate endDate) {
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be before or equal to end date");
        }

        // Fetch transactions within date range
        List<transactionHistory> transactions = transactionHistoryRepository
                .findByTransactionDateBetween(startDate, endDate);

        List<SalesReportDTO> reportList = new ArrayList<>();
        int no = 1;

        // Process each transaction
        for (transactionHistory transaction : transactions) {
            // Process each transaction detail
            for (transactionDetail detail : transaction.getTransactionDetails()) {
                SalesReportDTO reportDTO = SalesReportDTO.builder()
                        .no(no++)
                        .transactionId(transaction.getId())
                        .transactionDate(transaction.getTransactionDate())
                        .productName(detail.getProduct().getProductName())
                        .qty(detail.getQty())
                        .price(detail.getProduct().getPrice())
                        .totalPrice(detail.getTotalPrice())
                        .build();
                reportList.add(reportDTO);
            }
        }

        // Check if report data is empty
        if (reportList.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No transaction data found for the specified date range");
        }

        return reportList;
    }

    /**
     * Generates stock log report based on created date range
     * Retrieves stock log entries within the specified date range and formats them for reporting
     * 
     * @param startDate the start date for the report filter (inclusive)
     * @param endDate the end date for the report filter (inclusive)
     * @return List of StockLogReportDTO containing stock log data
     * @throws ResponseStatusException if start date is after end date or no data found
     */
    public List<StockLogReportDTO> getStockLogReport(LocalDate startDate, LocalDate endDate) {
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be before or equal to end date");
        }

        // Convert LocalDate to LocalDateTime (start of day and end of day)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Fetch stock logs within date range
        List<stockLog> stockLogs = stockLogRepository
                .findByCreatedAtBetween(startDateTime, endDateTime);

        List<StockLogReportDTO> reportList = new ArrayList<>();
        int no = 1;

        // Process each stock log
        for (stockLog log : stockLogs) {
            StockLogReportDTO reportDTO = StockLogReportDTO.builder()
                    .no(no++)
                    .logId(log.getId())
                    .productName(log.getProduct().getProductName())
                    .qty(log.getQuantityChange())
                    .logType(log.getLogType())
                    .createdAt(log.getCreatedAt())
                    .build();
            reportList.add(reportDTO);
        }

        // Check if report data is empty
        if (reportList.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No stock log data found for the specified date range");
        }

        return reportList;
    }
}

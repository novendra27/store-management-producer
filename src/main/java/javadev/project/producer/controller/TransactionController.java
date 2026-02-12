package javadev.project.producer.controller;

import javadev.project.producer.dto.transaction.TransactionRequest;
import javadev.project.producer.dto.transaction.TransactionResponse;
import javadev.project.producer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
@RequestMapping("/api/v1")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Processes a new transaction request and sends it to Kafka topic
     * Validates product availability and stock before sending to Kafka
     * The actual database insert will be handled by the consumer application
     * 
     * @param transactionRequest the transaction data including date and items list
     * @return TransactionResponse containing the same transaction data that was sent to Kafka
     */
    @PostMapping(path = "/transaction", produces = "application/json", consumes = "application/json")
    public TransactionResponse<TransactionRequest> createTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest) {

        TransactionRequest transaction = transactionService.createTransaction(transactionRequest);

        return TransactionResponse.<TransactionRequest>builder()
                .message("Transaction sent to Kafka successfully")
                .data(transaction)
                .build();
    }
}


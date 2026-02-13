package javadev.project.producer.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javadev.project.producer.dto.transaction.TransactionItemRequest;
import javadev.project.producer.dto.transaction.TransactionRequest;
import javadev.project.producer.entity.product;
import javadev.project.producer.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.sales-transaction}")
    private String topic;

    /**
     * Processes a new transaction request
     * Validates the transaction data, checks product availability and stock,
     * then sends the transaction data to Kafka topic for processing by consumer
     * 
     * @param transactionRequest the transaction data including date and items
     * @return TransactionRequest containing the same transaction data that will be
     *         sent to Kafka
     * @throws ConstraintViolationException if validation fails
     * @throws ResponseStatusException      if product is not found or insufficient
     *                                      stock
     */
    public TransactionRequest createTransaction(TransactionRequest transactionRequest) {
        // Log transaction received
        logger.info("Received transaction: date={}, items={}",
                transactionRequest.getTransactionDate(),
                transactionRequest.getItems().size());

        // Validate the incoming request
        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        double totalPrice = 0.0;

        // Validate each item
        for (TransactionItemRequest item : transactionRequest.getItems()) {
            // Validate product exists
            product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> {
                        logger.error("Product not found: product_id={}", item.getProductId());
                        return new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found with id: " + item.getProductId());
                    });

            logger.info("Processing item: product_id={}, qty={}, current_stock={}",
                    item.getProductId(),
                    item.getQty(),
                    product.getCurrentStock());

            // Check stock availability
            if (product.getCurrentStock() < item.getQty()) {
                logger.error("Insufficient stock: product_id={}, required={}, available={}",
                        item.getProductId(),
                        item.getQty(),
                        product.getCurrentStock());
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + product.getProductName() +
                                ". Available: " + product.getCurrentStock() + ", Requested: " + item.getQty());
            }

            // Calculate price for this item (BigDecimal * Integer)
            double itemPrice = product.getPrice().doubleValue() * item.getQty();
            totalPrice += itemPrice;

            // Check for low stock warning (after transaction would be processed)
            int stockAfterTransaction = product.getCurrentStock() - item.getQty();
            if (stockAfterTransaction <= LOW_STOCK_THRESHOLD) {
                logger.warn("Low stock alert: product_id={}, product_name={}, stock_after_transaction={}, threshold={}",
                        item.getProductId(),
                        product.getProductName(),
                        stockAfterTransaction,
                        LOW_STOCK_THRESHOLD);
            }
        }

        // Send transaction data to Kafka topic
        try {
            kafkaTemplate.send(topic, transactionRequest);
            logger.info("Transaction sent to Kafka successfully: date={}, items={}, estimated_total_price={}",
                    transactionRequest.getTransactionDate(),
                    transactionRequest.getItems().size(),
                    totalPrice);
        } catch (Exception e) {
            logger.error("Failed to send transaction to Kafka: date={}, error={}",
                    transactionRequest.getTransactionDate(),
                    e.getMessage(),
                    e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to send transaction to Kafka: " + e.getMessage());
        }

        return transactionRequest;
    }
}

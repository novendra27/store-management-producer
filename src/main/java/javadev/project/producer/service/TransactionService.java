package javadev.project.producer.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javadev.project.producer.dto.transaction.TransactionItemRequest;
import javadev.project.producer.dto.transaction.TransactionRequest;
import javadev.project.producer.entity.product;
import javadev.project.producer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class TransactionService {

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
        // Validate the incoming request
        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Validate each item
        for (TransactionItemRequest item : transactionRequest.getItems()) {
            // Validate product exists
            product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found with id: " + item.getProductId()));

            // Check stock availability
            if (product.getCurrentStock() < item.getQty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + product.getProductName() +
                                ". Available: " + product.getCurrentStock() + ", Requested: " + item.getQty());
            }
        }

        // Send transaction data to Kafka topic
        kafkaTemplate.send(topic, transactionRequest);

        return transactionRequest;
    }
}

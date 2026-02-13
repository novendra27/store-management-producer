package javadev.project.producer.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import javadev.project.producer.dto.product.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Handles ResponseStatusException thrown when a resource is not found or other
         * HTTP status errors occur
         * 
         * @param ex the ResponseStatusException that was thrown
         * @return ResponseEntity with error message and appropriate HTTP status
         */
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ProductResponse<String>> handleResponseStatusException(ResponseStatusException ex) {
                // Log based on status code
                if (ex.getStatusCode().is4xxClientError()) {
                        logger.warn("Client error: status={}, reason={}", ex.getStatusCode(), ex.getReason());
                } else {
                        logger.error("Server error: status={}, reason={}", ex.getStatusCode(), ex.getReason(), ex);
                }

                return ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ProductResponse.<String>builder()
                                                .message("Error")
                                                .data(null)
                                                .errors(ex.getReason())
                                                .build());
        }

        /**
         * Handles ConstraintViolationException when validation constraints are violated
         * Collects all validation error messages and returns them as a comma-separated
         * string
         * 
         * @param ex the ConstraintViolationException that was thrown
         * @return ResponseEntity with validation error messages and 400 Bad Request
         *         status
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ProductResponse<String>> handleConstraintViolationException(
                        ConstraintViolationException ex) {
                String errors = ex.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));

                logger.warn("Validation error: violations={}", errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ProductResponse.<String>builder()
                                                .message("Validation Error")
                                                .data(null)
                                                .errors(errors)
                                                .build());
        }

        /**
         * Handles MethodArgumentNotValidException when method argument validation fails
         * Extracts field-specific error messages from the binding result
         * 
         * @param ex the MethodArgumentNotValidException that was thrown
         * @return ResponseEntity with field validation errors and 400 Bad Request
         *         status
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ProductResponse<String>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex) {
                String errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                logger.warn("Method argument validation error: fields={}", errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ProductResponse.<String>builder()
                                                .message("Validation Error")
                                                .data(null)
                                                .errors(errors)
                                                .build());
        }

        /**
         * Handles HttpMessageNotReadableException when request body cannot be read or
         * parsed
         * Typically occurs with malformed JSON or invalid data types
         * 
         * @param ex the HttpMessageNotReadableException that was thrown
         * @return ResponseEntity with error message and 400 Bad Request status
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ProductResponse<String>> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {
                logger.warn("Invalid request body: error={}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ProductResponse.<String>builder()
                                                .message("Invalid Request Body")
                                                .data(null)
                                                .errors("Request body is malformed or invalid JSON format")
                                                .build());
        }

        /**
         * Handles MethodArgumentTypeMismatchException when a path variable or request
         * parameter has the wrong type
         * For example, passing a string when a Long is expected
         * 
         * @param ex the MethodArgumentTypeMismatchException that was thrown
         * @return ResponseEntity with error message and 400 Bad Request status
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ProductResponse<String>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {
                String error = String.format("Invalid value for parameter '%s'", ex.getName());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ProductResponse.<String>builder()
                                                .message("Invalid Parameter")
                                                .data(null)
                                                .errors(error)
                                                .build());
        }

        /**
         * Handles NoResourceFoundException when a requested endpoint does not exist
         * 
         * @param ex the NoResourceFoundException that was thrown
         * @return ResponseEntity with error message and 404 Not Found status
         */
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ProductResponse<String>> handleNoResourceFoundException(NoResourceFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ProductResponse.<String>builder()
                                                .message("Endpoint Not Found")
                                                .data(null)
                                                .errors("The requested endpoint does not exist")
                                                .build());
        }

        /**
         * Handles all other uncaught exceptions that are not specifically handled
         * Returns a generic error message without exposing technical details
         * 
         * @param ex the Exception that was thrown
         * @return ResponseEntity with generic error message and 500 Internal Server
         *         Error status
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ProductResponse<String>> handleGeneralException(Exception ex) {
                logger.error("Unexpected error occurred: type={}, message={}",
                                ex.getClass().getName(),
                                ex.getMessage(),
                                ex);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ProductResponse.<String>builder()
                                                .message("Internal Server Error")
                                                .data(null)
                                                .errors("An unexpected error occurred. Please try again later.")
                                                .build());
        }
}

package javadev.project.producer.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Transaction date is required")
    @JsonProperty("transaction_date")
    private LocalDate transactionDate;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<TransactionItemRequest> items;
}

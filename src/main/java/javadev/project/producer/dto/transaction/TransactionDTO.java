package javadev.project.producer.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private Integer id;

    @JsonProperty("transaction_date")
    private LocalDate transactionDate;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;
}

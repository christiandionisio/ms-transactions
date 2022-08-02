package com.example.mstransactions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    private String transactionId;
    private BigDecimal amount;
    private String originAccount;
    private String destinationAccount;
    private String transactionType;
    private LocalDate transactionDate;
    private String productId;
    private String productType;
    private Integer quotaNumber;
    private String commerceName;
}

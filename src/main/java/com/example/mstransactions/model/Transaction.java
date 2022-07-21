package com.example.mstransactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "transactions")
@Data
@AllArgsConstructor
public class Transaction {
    @Id
    private String transactionId;
    private BigDecimal amount;
    private String originAccount;
    private String destinationAccount;
    private String transactionType;
    private String transactionDate;
    private String productId;
    private String productType;
}

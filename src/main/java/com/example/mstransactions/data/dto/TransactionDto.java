package com.example.mstransactions.data.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionDto {
    private BigDecimal amount;
    private String originAccount;
    private String destinationAccount;
    private String transactionDate;
    private String productId;
    private String commerceName;
}

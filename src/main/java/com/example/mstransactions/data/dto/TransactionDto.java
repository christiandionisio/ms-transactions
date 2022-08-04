package com.example.mstransactions.data.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private String transactionId;
    private BigDecimal amount;
    private String originAccount;
    private String destinationAccount;
    private String transactionType;
    private LocalDateTime transactionDate;
    private String productId;
    private String productType;
    private Integer quotaNumber;
    private String commerceName;
    private Boolean withCommission;
    private BigDecimal commissionAmount;
}

package com.example.mstransactions.data.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionCommissionDto {
    private String transactionId;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime transactionDate;
    private String productType;
    private String commissionAmount;
}

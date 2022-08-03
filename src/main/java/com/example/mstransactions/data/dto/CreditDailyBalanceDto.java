package com.example.mstransactions.data.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditDailyBalanceDto {
    private String creditCardId;
    private BigDecimal averageDailyBalance;
    private BigDecimal totalBalance;
}

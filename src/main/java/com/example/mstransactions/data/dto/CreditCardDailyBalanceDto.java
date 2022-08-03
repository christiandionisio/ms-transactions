package com.example.mstransactions.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreditCardDailyBalanceDto {
    private String creditCardId;
    private BigDecimal averageDailyBalance;
    private BigDecimal totalBalance;
}

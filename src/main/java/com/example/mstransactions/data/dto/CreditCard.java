package com.example.mstransactions.data.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCard {
    private String creditCardId;
    private String creditCardNumber;
    private String expirationDate;
    private String cvv;
    private BigDecimal creditLimit;
    private String category;
    private String customerId;
}


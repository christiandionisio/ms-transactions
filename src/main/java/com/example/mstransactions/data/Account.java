package com.example.mstransactions.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    private String accountId;
    private String accountNumber;
    private String accountType;
    private String state;
    private BigDecimal balance;
    private String currency;
    private String createdAt;
    private String updatedAt;
    private String customerOwnerType;
    private String customerOwnerId;
}

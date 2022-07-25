package com.example.mstransactions.error;

public class AccountWithInsuficientBalanceException extends Exception{
    public AccountWithInsuficientBalanceException(String id) {
        super("productId: " + id + " with insuficient balance");
    }
}

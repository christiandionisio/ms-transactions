package com.example.mstransactions.error;

public class CreditCardWithInsuficientBalanceException extends Exception{
    public CreditCardWithInsuficientBalanceException(String id) {
        super("productId credit card : " + id + " with remaining credit insuficient");
    }
}

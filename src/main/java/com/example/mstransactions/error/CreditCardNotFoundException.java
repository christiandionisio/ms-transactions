package com.example.mstransactions.error;

public class CreditCardNotFoundException extends Exception{
    public CreditCardNotFoundException(String id) {
        super("productId credit card: " + id + " not found");
    }
}

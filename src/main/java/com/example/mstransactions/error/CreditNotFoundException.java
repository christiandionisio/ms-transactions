package com.example.mstransactions.error;

public class CreditNotFoundException extends Exception{
    public CreditNotFoundException(String id) {
        super("productId: " + id + " not found");
    }
}

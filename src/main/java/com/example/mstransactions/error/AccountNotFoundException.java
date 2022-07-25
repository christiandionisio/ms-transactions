package com.example.mstransactions.error;

public class AccountNotFoundException extends Exception{
    public AccountNotFoundException(String id) {
        super("productId: " + id + " not found");
    }
}

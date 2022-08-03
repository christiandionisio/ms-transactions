package com.example.mstransactions.error;

public class CreditCardServiceNotAvailableException extends Exception {
    public CreditCardServiceNotAvailableException() {
        super("Credit Card Service is not available");
    }
}

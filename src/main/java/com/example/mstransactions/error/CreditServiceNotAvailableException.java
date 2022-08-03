package com.example.mstransactions.error;

public class CreditServiceNotAvailableException extends Exception {
    public CreditServiceNotAvailableException() {
        super("Credit Service is not available");
    }
}

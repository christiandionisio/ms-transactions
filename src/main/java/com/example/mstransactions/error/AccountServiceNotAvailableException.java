package com.example.mstransactions.error;

public class AccountServiceNotAvailableException extends Exception {
    public AccountServiceNotAvailableException() {
        super("Account Service is not available");
    }
}

package com.example.mstransactions.error;

public class CreditPaymentAlreadyCompletedException extends Exception{
    public CreditPaymentAlreadyCompletedException(String id, Integer timeLimit) {
        super("productId credit: " + id + " credit already completed with " + timeLimit + " number of quotes");
    }
}

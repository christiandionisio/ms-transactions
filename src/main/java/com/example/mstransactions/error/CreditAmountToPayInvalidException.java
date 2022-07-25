package com.example.mstransactions.error;

public class CreditAmountToPayInvalidException extends Exception{
    public CreditAmountToPayInvalidException(String id) {
        super("productId credit: " + id + " with mount of the credit invalid");
    }
}

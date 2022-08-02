package com.example.mstransactions.data.enums;

public enum TransactionTypeEnum {
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL"),
    PAYMENT("PAYMENT"),
    CONSUMPTION("CONSUMPTION"),
    TRANSFER("TRANSFER");
    private String transactionType;
    TransactionTypeEnum(String transactionType){
        this.transactionType = transactionType;
    }

    public String getTransactionType(){
        return transactionType;
    }
}

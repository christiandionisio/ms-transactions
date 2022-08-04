package com.example.mstransactions.data.enums;

/**
 * TransactionTypeEnum Enum.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public enum TransactionTypeEnum {
  DEPOSIT("DEPOSIT"),
  WITHDRAWAL("WITHDRAWAL"),
  PAYMENT("PAYMENT"),
  CONSUMPTION("CONSUMPTION"),
  TRANSFER("TRANSFER");

  private String transactionType;

  TransactionTypeEnum(String transactionType) {
    this.transactionType = transactionType;
  }

  public String getTransactionType() {
    return transactionType;
  }
}

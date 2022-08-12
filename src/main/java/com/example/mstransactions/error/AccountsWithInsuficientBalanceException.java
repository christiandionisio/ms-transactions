package com.example.mstransactions.error;

/**
 * AccountWithInsuficientBalance Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class AccountsWithInsuficientBalanceException extends Exception {
  public AccountsWithInsuficientBalanceException(String customerId) {
    super("customerId: " + customerId + " with insuficient balance in associated accounts to debitCard product");
  }
}

package com.example.mstransactions.error;

/**
 * AccountWithInsuficientBalance Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class AccountWithInsuficientBalanceException extends Exception {
  public AccountWithInsuficientBalanceException(String id) {
    super("productId: " + id + " with insuficient balance");
  }
}

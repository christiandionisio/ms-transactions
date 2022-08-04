package com.example.mstransactions.error;

/**
 * AccountNotFound Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class AccountNotFoundException extends Exception {
  public AccountNotFoundException(String id) {
    super("productId: " + id + " not found");
  }
}

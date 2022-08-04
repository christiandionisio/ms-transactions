package com.example.mstransactions.error;


/**
 * AccountServiceNotAvailable Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class AccountServiceNotAvailableException extends Exception {
  public AccountServiceNotAvailableException() {
    super("Account Service is not available");
  }
}

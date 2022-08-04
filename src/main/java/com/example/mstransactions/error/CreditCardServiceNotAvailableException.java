package com.example.mstransactions.error;

/**
 * CreditCardServiceNotAvailable Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditCardServiceNotAvailableException extends Exception {
  public CreditCardServiceNotAvailableException() {
    super("Credit Card Service is not available");
  }
}

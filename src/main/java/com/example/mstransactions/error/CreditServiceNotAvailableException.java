package com.example.mstransactions.error;

/**
 * CreditServiceNotAvailableException Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditServiceNotAvailableException extends Exception {
  public CreditServiceNotAvailableException() {
    super("Credit Service is not available");
  }
}

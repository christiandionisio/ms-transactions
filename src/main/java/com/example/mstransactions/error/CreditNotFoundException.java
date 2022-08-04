package com.example.mstransactions.error;

/**
 * CreditNotFound Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditNotFoundException extends Exception {
  public CreditNotFoundException(String id) {
    super("productId: " + id + " not found");
  }
}

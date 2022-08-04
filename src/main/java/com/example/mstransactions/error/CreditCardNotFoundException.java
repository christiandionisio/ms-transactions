package com.example.mstransactions.error;

/**
 * CreditCardNotFound Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditCardNotFoundException extends Exception {
  public CreditCardNotFoundException(String id) {
    super("productId credit card: " + id + " not found");
  }
}

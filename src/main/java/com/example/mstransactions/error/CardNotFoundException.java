package com.example.mstransactions.error;

/**
 * CreditCardNotFound Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CardNotFoundException extends Exception {
  public CardNotFoundException(String id) {
    super("productId card: " + id + " not found");
  }
}

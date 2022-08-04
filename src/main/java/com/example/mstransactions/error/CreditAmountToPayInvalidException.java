package com.example.mstransactions.error;

/**
 * CreditAmountToPayInvalid Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditAmountToPayInvalidException extends Exception {
  public CreditAmountToPayInvalidException(String id) {
    super("productId credit: " + id + " with mount of the credit invalid");
  }
}

package com.example.mstransactions.error;

/**
 * CreditCardWithInsuficientBalance Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditCardWithInsuficientBalanceException extends Exception {
  public CreditCardWithInsuficientBalanceException(String id) {
    super("productId credit card : " + id + " with remaining credit insuficient");
  }
}

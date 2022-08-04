package com.example.mstransactions.error;

/**
 * CreditPaymentAlreadyCompleted Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class CreditPaymentAlreadyCompletedException extends Exception {
  public CreditPaymentAlreadyCompletedException(String id, Integer timeLimit) {
    super("productId credit: " + id + " credit already completed with "
            + timeLimit + " number of quotes");
  }
}

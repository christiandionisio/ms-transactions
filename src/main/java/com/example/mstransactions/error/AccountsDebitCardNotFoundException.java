package com.example.mstransactions.error;

/**
 * AccountNotFound Exception.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class AccountsDebitCardNotFoundException extends Exception {
  public AccountsDebitCardNotFoundException(String debitCardId) {
    super("Accounts not found for : " + debitCardId + " debitCard Product");
  }
}

package com.example.mstransactions.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 * CreditCard Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class Card {
  private String cardId;
  private String cardNumber;
  private String expirationDate;
  private String cvv;
  private BigDecimal creditLimit;
  private BigDecimal remainingCredit;
  private String category;
  private String customerId;
  private String cardType;
  private String mainAccountId;
  private boolean hasDebt;
}


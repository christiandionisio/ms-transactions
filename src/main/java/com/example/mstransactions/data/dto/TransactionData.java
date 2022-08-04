package com.example.mstransactions.data.dto;

import java.math.BigDecimal;

/**
* TransactionData Dto.
*
* @author Alisson Arteaga / Christian Dionisio
* @version 1.0
*/
public abstract class TransactionData {
  private BigDecimal amount;
  private String transactionType;
  private String transactionDate;
  private String productId;
  private String productType;

  /**
  * TransactionData Contructor.
  *
  * @author Alisson Arteaga / Christian Dionisio
  * @version 1.0
  */
  public TransactionData(BigDecimal amount, String transactionType,
                         String transactionDate, String productId, String productType) {
    this.amount = amount;
    this.transactionType = transactionType;
    this.transactionDate = transactionDate;
    this.productId = productId;
    this.productType = productType;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public String getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(String transactionDate) {
    this.transactionDate = transactionDate;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }
}

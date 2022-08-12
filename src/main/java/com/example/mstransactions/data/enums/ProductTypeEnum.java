package com.example.mstransactions.data.enums;

/**
 * ProductTypeEnum Enum.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public enum ProductTypeEnum {
  ACCOUNT("ACCOUNT"),
  CREDIT_CARD("CREDIT_CARD"),
  CREDIT("CREDIT"),

  DEBIT_CARD("DEBIT_CARD");

  private String value;

  ProductTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}

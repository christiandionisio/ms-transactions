package com.example.mstransactions.data.dto;

import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import java.math.BigDecimal;

/**
 * ConsumptionData Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class ConsumptionData extends TransactionData {
  private String commerceName;


  /**
   * ConsumptionData constructor.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public ConsumptionData(BigDecimal amount, String transactionDate,
                         String productId, String commerceName) {
    super(amount, TransactionTypeEnum.CONSUMPTION.getTransactionType(), transactionDate,
            productId, ProductTypeEnum.CREDIT_CARD.getValue());
    this.commerceName = commerceName;
  }

  public String getCommerceName() {
    return commerceName;
  }

  public void setCommerceName(String commerceName) {
    this.commerceName = commerceName;
  }
}

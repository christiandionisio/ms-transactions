package com.example.mstransactions.data.dto;

import com.example.mstransactions.data.enums.ProductTypeEnum;
import java.math.BigDecimal;

/**
 * TransferData Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public class TransferData extends TransactionData {
  private String originAccount;
  private String destinationAccount;
  private Boolean withCommission;
  private BigDecimal commissionAmount;

  /**
   * TransferData Constructor.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public TransferData(String operationType, BigDecimal amount,
                      String transactionDate, String productId,
                      String originAccount, String destinationAccount) {
    super(amount, operationType, transactionDate,
            productId, ProductTypeEnum.ACCOUNT.getValue());
    this.originAccount = originAccount;
    this.destinationAccount = destinationAccount;
  }

  public String getOriginAccount() {
    return originAccount;
  }

  public void setOriginAccount(String originAccount) {
    this.originAccount = originAccount;
  }

  public String getDestinationAccount() {
    return destinationAccount;
  }

  public void setDestinationAccount(String destinationAccount) {
    this.destinationAccount = destinationAccount;
  }

  public Boolean getWithCommission() {
    return withCommission;
  }

  public void setWithCommission(Boolean withCommission) {
    this.withCommission = withCommission;
  }

  public BigDecimal getCommissionAmount() {
    return commissionAmount;
  }

  public void setCommissionAmount(BigDecimal commissionAmount) {
    this.commissionAmount = commissionAmount;
  }
}

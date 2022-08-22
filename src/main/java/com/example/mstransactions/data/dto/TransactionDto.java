package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TransactionDto Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
  private String transactionId;
  private BigDecimal amount;
  private String originAccount;
  private String destinationAccount;
  private String transactionType;
  private String transactionDate;
  private String productId;
  private String productType;
  private Integer quotaNumber;
  private String commerceName;
  private Boolean withCommission;
  private BigDecimal commissionAmount;
}

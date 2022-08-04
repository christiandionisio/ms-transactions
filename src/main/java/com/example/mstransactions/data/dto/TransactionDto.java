package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * TransactionDto Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class TransactionDto {
  private String transactionId;
  private BigDecimal amount;
  private String originAccount;
  private String destinationAccount;
  private String transactionType;
  private LocalDateTime transactionDate;
  private String productId;
  private String productType;
  private Integer quotaNumber;
  private String commerceName;
  private Boolean withCommission;
  private BigDecimal commissionAmount;
}

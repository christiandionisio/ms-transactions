package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * TransactionCommissionDto Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class TransactionCommissionDto {
  private String transactionId;
  private BigDecimal amount;
  private String transactionType;
  private LocalDateTime transactionDate;
  private String productType;
  private BigDecimal commissionAmount;
}

package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CreditDailyBalance Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class CreditDailyBalanceDto {
  private String creditCardId;
  private BigDecimal averageDailyBalance;
  private BigDecimal totalBalance;
}

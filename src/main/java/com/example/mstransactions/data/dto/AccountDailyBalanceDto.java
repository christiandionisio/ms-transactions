package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AccountDailyBalance Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class AccountDailyBalanceDto {
  private String accountId;
  private BigDecimal averageDailyBalance;
  private BigDecimal totalBalance;
}

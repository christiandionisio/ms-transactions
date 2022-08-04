package com.example.mstransactions.data.dto;

import java.util.List;
import lombok.Data;

/**
 * DailyBalanceTemplateResponse Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class DailyBalanceTemplateResponse {
  private List<AccountDailyBalanceDto> accountDailyBalanceDtoList;
  private List<CreditCardDailyBalanceDto> creditCardDailyBalanceDtoList;
  private List<CreditDailyBalanceDto> creditDailyBalanceDtoList;
}

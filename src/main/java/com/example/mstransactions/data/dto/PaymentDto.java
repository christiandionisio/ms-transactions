package com.example.mstransactions.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentDto kafka received.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
  private BigDecimal amount;
  private String phoneNumberOrigin;
  private String phoneNumberDestination;
  private String dateTime;
}

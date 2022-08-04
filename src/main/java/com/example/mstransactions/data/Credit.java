package com.example.mstransactions.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Credit Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class Credit {
  private String creditId;
  private BigDecimal creditBalance;
  private String paymentDate;
  private Integer timeLimit;
  private String initialDate;
  private BigDecimal monthlyFee;
  private String creditType;
  private String customerId;
}

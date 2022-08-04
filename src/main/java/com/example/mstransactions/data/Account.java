package com.example.mstransactions.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Account Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class Account {
  private String accountId;
  private String accountNumber;
  private String accountType;
  private String state;
  private BigDecimal balance;
  private String currency;
  private String createdAt;
  private String updatedAt;
  private String customerOwnerType;
  private String customerOwnerId;
}

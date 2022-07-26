package com.example.mstransactions.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Transaction Document.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Document(collection = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
  @Id
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

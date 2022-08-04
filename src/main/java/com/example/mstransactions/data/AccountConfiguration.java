package com.example.mstransactions.data;

import lombok.Data;

/**
 * AccountConfiguration Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
public class AccountConfiguration {
  private String id;
  private String name;
  private Integer value;
  private String accountType;
}

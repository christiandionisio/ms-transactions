package com.example.mstransactions.data.dto;

import lombok.Getter;

/**
 * Filter Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Getter
public class FilterDto {
  private String productId;
  private String startDate;
  private String endDate;
}

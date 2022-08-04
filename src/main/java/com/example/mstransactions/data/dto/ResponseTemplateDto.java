package com.example.mstransactions.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ResponseTemplateDto Dto.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class ResponseTemplateDto {
  private Object response;
  private String errorMessage;
}

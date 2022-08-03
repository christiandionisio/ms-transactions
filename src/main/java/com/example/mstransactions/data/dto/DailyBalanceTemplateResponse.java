package com.example.mstransactions.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class DailyBalanceTemplateResponse {
    private List<AccountDailyBalanceDto> accountDailyBalanceDtoList;
}

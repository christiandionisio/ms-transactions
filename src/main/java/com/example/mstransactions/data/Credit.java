package com.example.mstransactions.data;

import lombok.Data;

import java.math.BigDecimal;

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

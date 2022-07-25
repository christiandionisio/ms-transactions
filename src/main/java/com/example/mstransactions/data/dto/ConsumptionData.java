package com.example.mstransactions.data.dto;

import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;

import java.math.BigDecimal;

public class ConsumptionData extends TransactionData{
    private String commerceName;


    public ConsumptionData(BigDecimal amount, String transactionDate, String productId, String commerceName) {
        super(amount, TransactionTypeEnum.CONSUMPTION.getTransactionType(), transactionDate, productId, ProductTypeEnum.CREDIT_CARD.getProductType());
        this.commerceName = commerceName;
    }

    public String getCommerceName() {
        return commerceName;
    }

    public void setCommerceName(String commerceName) {
        this.commerceName = commerceName;
    }
}

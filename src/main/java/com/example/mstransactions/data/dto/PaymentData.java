package com.example.mstransactions.data.dto;

import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;

import java.math.BigDecimal;

public class PaymentData extends TransactionData{
    private Integer quotaNumber;


    public PaymentData(BigDecimal amount, String transactionDate, String productId, Integer quotaNumber) {
        super(amount, TransactionTypeEnum.PAYMENT.getTransactionType(), transactionDate, productId, ProductTypeEnum.CREDIT.getProductType());
        this.quotaNumber = quotaNumber;
    }

    public Integer getQuotaNumber() {
        return quotaNumber;
    }

    public void setQuotaNumber(Integer quotaNumber) {
        this.quotaNumber = quotaNumber;
    }
}

package com.example.mstransactions.data.dto;

import com.example.mstransactions.data.enums.ProductTypeEnum;

import java.math.BigDecimal;

public class TransferData extends TransactionData{
    private String originAccount;
    private String destinationAccount;
    private Boolean withCommission;

    public TransferData(String operationType, BigDecimal amount, String transactionDate, String productId, String originAccount, String destinationAccount) {
        super(amount, operationType, transactionDate, productId, ProductTypeEnum.ACCOUNT.getProductType());
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public Boolean getWithCommission() {
        return withCommission;
    }

    public void setWithCommission(Boolean withCommission) {
        this.withCommission = withCommission;
    }
}

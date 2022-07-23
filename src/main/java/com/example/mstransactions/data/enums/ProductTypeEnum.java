package com.example.mstransactions.data.enums;

public enum ProductTypeEnum {
    ACCOUNT("ACCOUNT"),
    CREDIT_CARD("CREDIT_CARD"),
    CREDIT("CREDIT");
    private String productType;
    ProductTypeEnum(String productType){
        this.productType = productType;
    }

    public String getProductType(){
        return productType;
    }
}

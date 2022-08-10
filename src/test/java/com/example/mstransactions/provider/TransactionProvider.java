package com.example.mstransactions.provider;

import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionProvider {

  public static List<Transaction> getTransactionList() {
    List<Transaction> creditList = new ArrayList<>();
    creditList.add(getTransaction());
    return creditList;
  }

  public static Transaction getTransaction() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(5000));
    transaction.setTransactionType(TransactionTypeEnum.DEPOSIT.getTransactionType());
    transaction.setTransactionDate(LocalDateTime.of(2022, 2, 13, 15, 56));
    transaction.setProductId("1");
    transaction.setProductType(ProductTypeEnum.ACCOUNT.getProductType());
    return transaction;
  }

  public static TransactionDto getTransactionDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(5000))
            .transactionType(TransactionTypeEnum.DEPOSIT.getTransactionType())
            .transactionDate(LocalDateTime.of(2022, 2, 13, 15, 56))
            .productId("1")
            .productType(ProductTypeEnum.ACCOUNT.getProductType())
            .build();
  }

  public static Transaction getTransactionDeposit() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(200));
    transaction.setTransactionType(TransactionTypeEnum.DEPOSIT.getTransactionType());
    transaction.setTransactionDate(LocalDateTime.of(2022, 2, 15, 15, 56));
    transaction.setProductId("1");
    transaction.setProductType(ProductTypeEnum.ACCOUNT.getProductType());
    return transaction;
  }

  public static TransactionDto getTransactionDepositDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(200))
            .transactionType(TransactionTypeEnum.DEPOSIT.getTransactionType())
            .transactionDate(LocalDateTime.of(2022, 2, 15, 15, 56))
            .productId("1")
            .productType(ProductTypeEnum.ACCOUNT.getProductType())
            .build();
  }

  public static Transaction getTransactionWithdrawal() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(100));
    transaction.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getTransactionType());
    transaction.setTransactionDate(LocalDateTime.of(2022, 1, 15, 15, 56));
    transaction.setProductId("1");
    transaction.setProductType(ProductTypeEnum.ACCOUNT.getProductType());
    return transaction;
  }

  public static TransactionDto getTransactionWithdrawalDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(100))
            .transactionType(TransactionTypeEnum.WITHDRAWAL.getTransactionType())
            .transactionDate(LocalDateTime.of(2022, 1, 15, 15, 56))
            .productId("1")
            .productType(ProductTypeEnum.ACCOUNT.getProductType())
            .build();
  }

  public static Transaction getTransactionPayment() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(50.23));
    transaction.setTransactionType(TransactionTypeEnum.PAYMENT.getTransactionType());
    transaction.setTransactionDate(LocalDateTime.of(2022, 2, 15, 15, 56));
    transaction.setQuotaNumber(1);
    transaction.setProductId("1");
    transaction.setProductType(ProductTypeEnum.CREDIT.getProductType());
    return transaction;
  }

  public static TransactionDto getTransactionPaymentDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(50.23))
            .transactionType(TransactionTypeEnum.PAYMENT.getTransactionType())
            .transactionDate(LocalDateTime.of(2022, 2, 15, 15, 56))
            .quotaNumber(1)
            .productId("1")
            .productType(ProductTypeEnum.CREDIT.getProductType())
            .build();
  }
}

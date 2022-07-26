package com.example.mstransactions.provider;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.Card;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionProvider {

  public static final String PRODUCT_ID_DEPOSIT = "D01";
  public static final String PRODUCT_ID_WITHDRAWAL = "W01";
  public static final String PRODUCT_ID_CREDIT = "C01";
  public static final String PRODUCT_ID_CREDIT_CARD = "CC01";

  public static final String PRODUCT_ID = "P01";

  private static final LocalDateTime TRANSACTION_DATE_TIME = LocalDateTime.of(2022, 2, 1, 0, 0);
  private static final String TRANSACTION_DATE = "13/02/2022";

  public static List<Transaction> getTransactionList() {
    List<Transaction> creditList = new ArrayList<>();
    creditList.add(getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT));
    return creditList;
  }

  public static Transaction getTransaction(ProductTypeEnum productTypeEnum, TransactionTypeEnum transactionTypeEnum) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(10));
    transaction.setTransactionType(transactionTypeEnum.getTransactionType());
    transaction.setTransactionDate(LocalDateTime.of(2022, 2, 13, 15, 56));
    transaction.setProductId(PRODUCT_ID);
    transaction.setProductType(productTypeEnum.getValue());
    return transaction;
  }

  public static TransactionDto getTransactionDto(ProductTypeEnum productTypeEnum, TransactionTypeEnum transactionTypeEnum) {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(10))
            .transactionType(transactionTypeEnum.getTransactionType())
            .transactionDate("22/07/2022")
            .productId(PRODUCT_ID)
            .productType(productTypeEnum.getValue())
            .build();
  }

  public static Transaction getTransactionDeposit() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(200));
    transaction.setTransactionType(TransactionTypeEnum.DEPOSIT.getTransactionType());
    transaction.setTransactionDate(TRANSACTION_DATE_TIME);
    transaction.setProductId(PRODUCT_ID_DEPOSIT);
    transaction.setProductType(ProductTypeEnum.ACCOUNT.getValue());
    return transaction;
  }

  public static TransactionDto getTransactionDepositDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(200))
            .transactionType(TransactionTypeEnum.DEPOSIT.getTransactionType())
            .transactionDate(TRANSACTION_DATE)
            .productId(PRODUCT_ID_DEPOSIT)
            .productType(ProductTypeEnum.ACCOUNT.getValue())
            .build();
  }

  public static Transaction getTransactionWithdrawal() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(100));
    transaction.setTransactionType(TransactionTypeEnum.WITHDRAWAL.getTransactionType());
    transaction.setTransactionDate(TRANSACTION_DATE_TIME);
    transaction.setProductId(PRODUCT_ID_WITHDRAWAL);
    transaction.setProductType(ProductTypeEnum.ACCOUNT.getValue());
    return transaction;
  }

  public static TransactionDto getTransactionWithdrawalDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(100))
            .transactionType(TransactionTypeEnum.WITHDRAWAL.getTransactionType())
            .transactionDate(TRANSACTION_DATE)
            .productId(PRODUCT_ID_WITHDRAWAL)
            .productType(ProductTypeEnum.ACCOUNT.getValue())
            .build();
  }

  public static Transaction getTransactionPayment() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(50.23));
    transaction.setTransactionType(TransactionTypeEnum.PAYMENT.getTransactionType());
    transaction.setTransactionDate(TRANSACTION_DATE_TIME);
    transaction.setQuotaNumber(1);
    transaction.setProductId(PRODUCT_ID_CREDIT);
    transaction.setProductType(ProductTypeEnum.CREDIT.getValue());
    return transaction;
  }

  public static TransactionDto getTransactionPaymentDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(50.23))
            .transactionType(TransactionTypeEnum.PAYMENT.getTransactionType())
            .transactionDate(TRANSACTION_DATE)
            .quotaNumber(1)
            .productId(PRODUCT_ID_CREDIT)
            .productType(ProductTypeEnum.CREDIT.getValue())
            .build();
  }

  public static Transaction getTransactionConsumption() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId("1");
    transaction.setAmount(BigDecimal.valueOf(50.23));
    transaction.setTransactionType(TransactionTypeEnum.CONSUMPTION.getTransactionType());
    transaction.setTransactionDate(TRANSACTION_DATE_TIME);
    transaction.setCommerceName("LINIO.PE");
    transaction.setProductId(PRODUCT_ID_CREDIT_CARD);
    transaction.setProductType(ProductTypeEnum.CREDIT_CARD.getValue());
    return transaction;
  }

  public static TransactionDto getTransactionConsumptionDto() {
    return TransactionDto.builder()
            .transactionId("1")
            .amount(BigDecimal.valueOf(50.23))
            .transactionType(TransactionTypeEnum.CONSUMPTION.getTransactionType())
            .transactionDate(TRANSACTION_DATE)
            .commerceName("LINIO.PE")
            .productId(PRODUCT_ID_CREDIT_CARD)
            .productType(ProductTypeEnum.CREDIT_CARD.getValue())
            .build();
  }

  public static Card getCard() {
    Card creditCard = new Card();
    creditCard.setCardId("1");
    creditCard.setCardNumber("11111111111111");
    creditCard.setExpirationDate("07/29");
    creditCard.setCvv("123");
    creditCard.setCreditLimit(BigDecimal.valueOf(5000));
    creditCard.setRemainingCredit(BigDecimal.valueOf(450.23));
    creditCard.setCategory("PLATINUM");
    creditCard.setCardType("CREDIT_CARD");
    creditCard.setMainAccountId("2");
    creditCard.setCustomerId("1");
    return creditCard;
  }

  public static Account getAccount() {
    Account account = new Account();
    account.setAccountId("1");
    account.setAccountNumber("123456");
    account.setAccountType("CORRIENTE");
    account.setBalance(BigDecimal.valueOf(1000));
    account.setCurrency("USD");
    account.setCustomerOwnerId("123");
    account.setCustomerOwnerType("PERSONAL");
    return account;
  }
}

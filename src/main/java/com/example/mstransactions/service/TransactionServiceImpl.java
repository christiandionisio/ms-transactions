package com.example.mstransactions.service;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.Card;
import com.example.mstransactions.data.dto.AccountDailyBalanceDto;
import com.example.mstransactions.data.dto.ConsumptionData;
import com.example.mstransactions.data.dto.CreditCardDailyBalanceDto;
import com.example.mstransactions.data.dto.CreditDailyBalanceDto;
import com.example.mstransactions.data.dto.DailyBalanceTemplateResponse;
import com.example.mstransactions.data.dto.FilterDto;
import com.example.mstransactions.data.dto.PaymentData;
import com.example.mstransactions.data.dto.TransactionCommissionDto;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.dto.TransferData;
import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.error.AccountServiceNotAvailableException;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.AccountsWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditAmountToPayInvalidException;
import com.example.mstransactions.error.CreditCardServiceNotAvailableException;
import com.example.mstransactions.error.CreditCardWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditPaymentAlreadyCompletedException;
import com.example.mstransactions.error.CreditServiceNotAvailableException;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.repo.TransactionRepo;
import com.example.mstransactions.utils.TransactionUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * TransactionServiceImpl Service Implementation.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionRepo repo;

  @Autowired
  private ReactiveCircuitBreakerFactory circuitBreakerFactory;

  @Autowired
  private TransactionUtil transactionUtil;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private static final DateTimeFormatter FORMATTER_DATE_TIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final String ACCOUNT_SERVICE_NAME = "account-service";


  @Override
  public Flux<Transaction> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<Transaction> findById(String id) {
    return repo.findById(id);
  }

  @Override
  public Mono<Transaction> create(Transaction transaction) {
    return repo.save(transaction);
  }

  @Override
  public Mono<Transaction> update(Transaction transaction) {
    return repo.save(transaction);
  }

  @Override
  public Mono<Void> delete(String transactionId) {
    return repo.deleteById(transactionId);
  }

  @Override
  public Mono<Transaction> makeDeposit(TransactionDto transaction) {
    TransferData transferData = new TransferData(TransactionTypeEnum.DEPOSIT.getTransactionType(),
        transaction.getAmount(), String.valueOf(transaction.getTransactionDate()),
        transaction.getProductId(), null, null);

    return transactionUtil.findAccountById(transaction.getProductId())
        .flatMap(accountTransition -> {
          LocalDate localDate = LocalDate.parse(
              String.valueOf(transaction.getTransactionDate()), FORMATTER);

          return repo.findByTransactionDateBetweenAndProductId(
                  localDate.withDayOfMonth(1).atStartOfDay(),
                  localDate.withDayOfMonth(localDate.getMonth().length(
                      localDate.isLeapYear())).atStartOfDay(),
                  transaction.getProductId())
              .count()
              .flatMap(numberOfTransactions ->
                  transactionUtil.findByAccountTypeAndName(
                          accountTransition.getAccountType(), "maximoTransacciones")
                      .flatMap(accountConfiguration ->
                          (numberOfTransactions > accountConfiguration.getValue())
                              ? Mono.just(true)
                              : Mono.just(false))
                      .flatMap(isOutOfMaxTransactions ->
                          transactionUtil.setBalanceCommisionToDeposit(
                              isOutOfMaxTransactions, accountTransition,
                              transaction, transferData)
                      )
              )
              .flatMap(finalAcount -> transactionUtil.updateAccountBalance(finalAcount)
                  .flatMap(update -> this.saveOperation(transferData))
              );
        });
  }

  @Override
  public Mono<Transaction> makeWithdrawal(TransactionDto transaction) {
    TransferData transferData = new TransferData(
        TransactionTypeEnum.WITHDRAWAL.getTransactionType(),
        transaction.getAmount(), String.valueOf(transaction.getTransactionDate()),
        transaction.getProductId(), null, null);

    return transactionUtil.findAccountById(transaction.getProductId()).flatMap(accountTransition -> {
      LocalDate localDate = LocalDate.parse(String.valueOf(transaction.getTransactionDate()), FORMATTER);
      return repo.findByTransactionDateBetweenAndProductId(
              localDate.withDayOfMonth(1).atStartOfDay(),
              localDate.withDayOfMonth(localDate.getMonth().length(
                  localDate.isLeapYear())).atStartOfDay(),
              transaction.getProductId())
          .count()
          .flatMap(numberOfTransactions ->
              transactionUtil.findByAccountTypeAndName(
                      accountTransition.getAccountType(), "maximoTransacciones")
                  .flatMap(accountConfiguration ->
                      (numberOfTransactions > accountConfiguration.getValue())
                          ? Mono.just(true)
                          : Mono.just(false))
                  .flatMap(isOutOfMaxTransactions ->
                      transactionUtil.setBalanceCommissionToWithdrawal(
                          isOutOfMaxTransactions, accountTransition,
                          transaction, transferData)
                  )
          )
          .flatMap(finalAcount -> transactionUtil.updateAccountBalance(finalAcount)
              .flatMap(update -> this.saveOperation(transferData))
          );
    });
  }

  /**
   * Save the transaction operation.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public Mono<Transaction> saveOperation(TransferData transferData) {
    LocalDateTime localDateTime = LocalDateTime.now();
    if (transferData.getTransactionDate() != null) {
      localDateTime = LocalDate.parse(
          transferData.getTransactionDate(), FORMATTER).atStartOfDay();
    }
    Transaction saveTransaction = Transaction.builder()
        .transactionDate(localDateTime)
        .amount(transferData.getAmount())
        .transactionType(transferData.getTransactionType())
        .originAccount(transferData.getOriginAccount())
        .destinationAccount(transferData.getDestinationAccount())
        .productId(transferData.getProductId())
        .productType(transferData.getProductType())
        .withCommission(transferData.getWithCommission())
        .commissionAmount(transferData.getCommissionAmount())
        .build();
    return repo.save(saveTransaction);
  }

  @Override
  public Mono<Transaction> makePayment(TransactionDto transactionDto) {

    return transactionUtil.findCreditById(transactionDto.getProductId()).flatMap(credit -> {
      if (transactionDto.getAmount().compareTo(credit.getMonthlyFee()) == 0) {
        return repo.countTransactionsByProductId(transactionDto.getProductId())
            .flatMap(count -> {
              Integer quotaNumber = count.intValue() + 1;
              if (quotaNumber.equals(credit.getTimeLimit() + 1)) {
                return Mono.error(
                    new CreditPaymentAlreadyCompletedException(
                        credit.getCreditId(), credit.getTimeLimit()));
              }
              PaymentData paymentData = new PaymentData(
                  transactionDto.getAmount(),
                  String.valueOf(transactionDto.getTransactionDate()),
                  transactionDto.getProductId(), quotaNumber);
              return this.savePayment(paymentData);
            });
      } else {
        return Mono.error(new CreditAmountToPayInvalidException(credit.getCreditId()));
      }
    });
  }

  /**
   * Save the payment transaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public Mono<Transaction> savePayment(PaymentData paymentData) {
    Transaction saveTransaction = Transaction.builder()
        .transactionDate(LocalDate.parse(
            paymentData.getTransactionDate(), FORMATTER).atStartOfDay())
        .amount(paymentData.getAmount())
        .transactionType(paymentData.getTransactionType())
        .productId(paymentData.getProductId())
        .productType(paymentData.getProductType())
        .quotaNumber(paymentData.getQuotaNumber()).build();

    return repo.save(saveTransaction);
  }

  @Override
  public Mono<Transaction> makeConsumption(TransactionDto transaction) {
    ConsumptionData consumptionData = new ConsumptionData(
        transaction.getAmount(),
        String.valueOf(transaction.getTransactionDate()), transaction.getProductId(),
        transaction.getCommerceName());

    return transactionUtil.findCreditCardById(transaction.getProductId()).flatMap(creditCard -> {
      BigDecimal creditLimit = creditCard.getCreditLimit();
      BigDecimal remainingCredit = creditCard.getRemainingCredit();

      if (creditLimit.compareTo(transaction.getAmount()) != -1
          && remainingCredit.compareTo(transaction.getAmount()) != -1) {
        creditCard.setRemainingCredit(remainingCredit.subtract(transaction.getAmount()));
        return transactionUtil.updateCreditCardLimit(creditCard)
            .flatMap(update -> this.saveConsumption(consumptionData));
      } else {
        return Mono.error(
            new CreditCardWithInsuficientBalanceException(
                creditCard.getCardId()));
      }
    });
  }

  @Override
  public Flux<Transaction> findTransactionsByProductId(String productId) {
    return repo.findAllByProductId(productId);
  }

  @Override
  public Flux<Transaction> findTransactionsByProductTypeAndProductId(
      String productType, String productId) {
    return repo.findAllByProductTypeAndProductId(productType, productId);
  }

  /**
   * TransactionServiceImpl Service Implementation.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public Mono<Transaction> transferBetweenAccounts(TransactionDto transactionDto) {
    TransferData transferData = new TransferData(TransactionTypeEnum.TRANSFER.getTransactionType(),
        transactionDto.getAmount(),
        String.valueOf(transactionDto.getTransactionDate()), transactionDto.getProductId(),
        transactionDto.getOriginAccount(), transactionDto.getDestinationAccount());

    return transactionUtil.findAccountById(
            transferData.getOriginAccount()).flatMap(originAccount -> {
              BigDecimal actualAmount = originAccount.getBalance();
              if (actualAmount.compareTo(transactionDto.getAmount()) != -1) {
                originAccount.setBalance(actualAmount.subtract(transactionDto.getAmount()));
                return transactionUtil.updateAccountBalance(originAccount);
              }  else {
                return Mono.error(
                    new AccountWithInsuficientBalanceException(
                    originAccount.getAccountId()));
              }
            })
            .transform(it -> circuitBreakerFactory.create(ACCOUNT_SERVICE_NAME).run(it,
              throwable -> Mono.error(new AccountServiceNotAvailableException())))
            .flatMap(accountOriginUpdated ->
                transactionUtil.findAccountById(
                        transferData.getDestinationAccount()).flatMap(destinationAccount -> {
                          destinationAccount.setBalance(destinationAccount.getBalance().add(transactionDto.getAmount()));
                          return transactionUtil.updateAccountBalance(destinationAccount)
                          .flatMap(update -> this.saveOperation(transferData));
                        })
                .transform(it -> circuitBreakerFactory.create(ACCOUNT_SERVICE_NAME).run(it,
                    throwable -> Mono.error(new AccountServiceNotAvailableException()))
                )
        );

  }

  /**
   * Save transaction for comsumption request.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public Mono<Transaction> saveConsumption(ConsumptionData consumptionData) {
    Transaction saveTransaction = Transaction.builder()
        .transactionDate(LocalDate.parse(
            consumptionData.getTransactionDate(), FORMATTER).atStartOfDay())
        .amount(consumptionData.getAmount())
        .transactionType(consumptionData.getTransactionType())
        .productId(consumptionData.getProductId())
        .productType(consumptionData.getProductType())
        .commerceName(consumptionData.getCommerceName()).build();

    return repo.save(saveTransaction);
  }

  @Override
  public Flux<TransactionCommissionDto> getTransactionsWithCommissions(FilterDto filterDto) {
    LocalDateTime startDate = LocalDateTime.parse(
        filterDto.getStartDate(), FORMATTER_DATE_TIME).minusSeconds(1);
    LocalDateTime endDate = LocalDateTime.parse(
        filterDto.getEndDate(), FORMATTER_DATE_TIME).plusSeconds(1);

    return repo.findByProductIdAndTransactionDateBetweenAndWithCommissionIsTrue(
            filterDto.getProductId(), startDate, endDate)
        .map(this::createTransactionCommissionDto);
  }

  @Override
  public Mono<DailyBalanceTemplateResponse> getDailyBalanceTemplate(String customerId) {
    DailyBalanceTemplateResponse dailyBalanceTemplateResponse = new DailyBalanceTemplateResponse();
    dailyBalanceTemplateResponse.setAccountDailyBalanceDtoList(new ArrayList<>());
    dailyBalanceTemplateResponse.setCreditCardDailyBalanceDtoList(new ArrayList<>());
    dailyBalanceTemplateResponse.setCreditDailyBalanceDtoList(new ArrayList<>());

    long daysBetween = ChronoUnit.DAYS.between(
        LocalDate.now().withDayOfMonth(1), LocalDateTime.now());

    return TransactionUtil.findAccountByCustomerId(customerId)
        .map(account -> {
          AccountDailyBalanceDto accountDailyBalanceDto =
              new AccountDailyBalanceDto(account.getAccountId(),
                  account.getBalance().divide(
                      BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP),
                  account.getBalance());
          dailyBalanceTemplateResponse
              .getAccountDailyBalanceDtoList().add(accountDailyBalanceDto);
          return account;
        })
        .collectList()
        .transform(it -> circuitBreakerFactory.create(ACCOUNT_SERVICE_NAME).run(it,
            throwable -> Mono.error(new AccountServiceNotAvailableException()))
        )
        .flatMap(accounts -> TransactionUtil.findCreditCardByCustomerId(customerId)
            .map(creditCard -> {
              CreditCardDailyBalanceDto creditCardDailyBalanceDto =
                  new CreditCardDailyBalanceDto(creditCard.getCardId(),
                      creditCard.getCreditLimit().divide(
                          BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP),
                      creditCard.getCreditLimit());
              dailyBalanceTemplateResponse
                  .getCreditCardDailyBalanceDtoList().add(creditCardDailyBalanceDto);
              return creditCard;
            }).collectList()
            .transform(it -> circuitBreakerFactory.create("credit-card-service").run(it,
                throwable -> Mono.error(new CreditCardServiceNotAvailableException()))
            )
        )
        .flatMap(creditCards -> transactionUtil.findCreditByCustomerId(customerId)
            .map(credit -> {
              CreditDailyBalanceDto creditDailyBalanceDto =
                  new CreditDailyBalanceDto(credit.getCreditId(),
                      credit.getCreditBalance().divide(
                          BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP),
                      credit.getCreditBalance());
              dailyBalanceTemplateResponse
                  .getCreditDailyBalanceDtoList().add(creditDailyBalanceDto);
              return credit;
            }).collectList()
            .transform(it -> circuitBreakerFactory.create("credit-service").run(it,
                throwable -> Mono.error(new CreditServiceNotAvailableException()))
            )
        )
        .flatMap(finalResponse -> Mono.just(dailyBalanceTemplateResponse));
  }

  @Override
  public Flux<Transaction> getReportByProductType(String productType, String startDate,
                                                  String endDate) {
    LocalDate startDateTime = LocalDate.parse(startDate, FORMATTER);
    LocalDate endDateTime = LocalDate.parse(endDate, FORMATTER);
    return repo.findByProductTypeAndTransactionDateBetween(productType, startDateTime.atStartOfDay(),
        endDateTime.atStartOfDay());
  }

  /**
   * Get TransactionCommissionDto from Transaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  public TransactionCommissionDto createTransactionCommissionDto(Transaction transaction) {
    TransactionCommissionDto transactionCommissionDto = new TransactionCommissionDto();
    transactionCommissionDto.setTransactionId(transaction.getTransactionId());
    transactionCommissionDto.setAmount(transaction.getAmount());
    transactionCommissionDto.setTransactionType(transaction.getTransactionType());
    transactionCommissionDto.setTransactionDate(transaction.getTransactionDate());
    transactionCommissionDto.setProductType(transaction.getProductType());
    transactionCommissionDto.setCommissionAmount(transaction.getCommissionAmount());
    return transactionCommissionDto;
  }

  @Override
  public Mono<Transaction> makeWithdrawalOfDebitCard(TransactionDto transactionDto, String customerId) {
    TransferData transferData = new TransferData(
        TransactionTypeEnum.WITHDRAWAL.getTransactionType(),
        transactionDto.getAmount(), transactionDto.getTransactionDate(),
        transactionDto.getProductId(), null, null);
    transferData.setProductType(ProductTypeEnum.DEBIT_CARD.getValue());

    return transactionUtil.findDebitCardByCustomerId(customerId, transactionDto.getProductId())
        .map(Card::getMainAccountId)
        .flatMap(accountId -> {
          System.out.println("accountId obtenido: " + accountId);
          return transactionUtil.findAccountById(accountId)
              .flatMap(account -> {
                BigDecimal actualAmount = transactionDto.getAmount();

                if (account.getBalance().compareTo(actualAmount) != -1) {
                  account.setBalance(account.getBalance().subtract(actualAmount));
                  transferData.setDestinationAccount(account.getAccountId());
                  return Mono.just(account);
                } else {
                  Mono<Account> accountMono = transactionUtil.findAccountsByCustomerIdAndDebitCardId(customerId, transactionDto.getProductId())
                      .filter(account1 -> (!account1.getAccountId().equals((accountId))))
                      .filter(account2 -> (account2.getBalance().compareTo(transactionDto.getAmount()) != -1))
                      .map(c -> {
                        c.setBalance(c.getBalance().subtract(actualAmount));
                        transferData.setDestinationAccount(c.getAccountId());
                        return c;
                      }).next();
                  return accountMono.defaultIfEmpty(new Account());
                }
              });
        }).flatMap(finalAccount -> {
          if (finalAccount.getAccountId() == null) {
            return Mono.error(
                new AccountsWithInsuficientBalanceException(customerId));
          } else {
            return transactionUtil.updateAccountBalance(finalAccount)
                .flatMap(update -> this.saveOperation(transferData));
          }
        });
  }

  @Override
  public Flux<Transaction> findLastTenTransactionsByProductTypeAndProductId(String productType, String productId) {
    return repo.findAllByProductTypeAndProductIdOrderByTransactionDateDesc(productType, productId).take(10);
  }
}

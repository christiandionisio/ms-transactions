package com.example.mstransactions.service;

import com.example.mstransactions.data.dto.ConsumptionData;
import com.example.mstransactions.data.dto.TransferData;
import com.example.mstransactions.data.dto.PaymentData;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.error.*;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.repo.TransactionRepo;
import com.example.mstransactions.utils.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionRepo repo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        TransferData transferData = new TransferData(TransactionTypeEnum.DEPOSIT.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                null, null);

        return TransactionUtil.findAccountById(transaction.getProductId())
                .flatMap(accountTransition -> {
                    LocalDate localDate = LocalDate.parse(transaction.getTransactionDate(), FORMATTER);
                    return repo.findByTransactionDateBetween(localDate.withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                            localDate.withDayOfMonth(localDate.getMonth().length(localDate.isLeapYear())).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            .count()
                            .flatMap(numberOfTransactions -> TransactionUtil.findByAccountTypeAndName(accountTransition.getAccountType(), "maximoTransacciones")
                                    .flatMap(accountConfiguration -> (numberOfTransactions > accountConfiguration.getValue())
                                                ? Mono.just(true)
                                                : Mono.just(false))
                                    .flatMap(isOutOfMaxTransactions -> {
                                        if (Boolean.TRUE.equals(isOutOfMaxTransactions)) {
                                            return TransactionUtil.findByAccountTypeAndName(accountTransition.getAccountType(), "commision")
                                                    .flatMap(accountConfiguration -> {
                                                        BigDecimal commision = BigDecimal.valueOf(accountConfiguration.getValue()*0.01);
                                                        BigDecimal commisionAmount = transaction.getAmount().multiply(commision);
                                                        BigDecimal finalAmount = transaction.getAmount().subtract(commisionAmount);
                                                        accountTransition.setBalance(accountTransition.getBalance().add(finalAmount));
                                                        transferData.setWithCommission(true);
                                                        return Mono.just(accountTransition);
                                                    });
                                        } else {
                                            BigDecimal actualAmount = accountTransition.getBalance();
                                            accountTransition.setBalance(actualAmount.add(transaction.getAmount()));
                                            transferData.setWithCommission(false);
                                            return Mono.just(accountTransition);
                                        }
                                    })
                            )
                            .flatMap(finalAcount -> TransactionUtil.updateAccountBalance(finalAcount)
                                    .flatMap(update -> this.saveOperation(transferData))
                            );
                });
    }

    @Override
    public Mono<Transaction> makeWithdrawal(TransactionDto transaction) {
        TransferData transferData = new TransferData(TransactionTypeEnum.WITHDRAWAL.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                null, null);

        return TransactionUtil.findAccountById(transaction.getProductId()).flatMap(account -> {
            BigDecimal actualAmount = account.getBalance();
            if(actualAmount.compareTo(transaction.getAmount()) != -1){
                account.setBalance(actualAmount.subtract(transaction.getAmount()));
                return TransactionUtil.updateAccountBalance(account)
                        .flatMap(update -> this.saveOperation(transferData));
            }else{
                return Mono.error(new AccountWithInsuficientBalanceException(account.getAccountId()));
            }
        });
    }

    public Mono<Transaction> saveOperation(TransferData transferData){
        Transaction saveTransaction = Transaction.builder()
                .transactionDate(LocalDate.parse(transferData.getTransactionDate(), FORMATTER))
                .amount(transferData.getAmount())
                .transactionType(transferData.getTransactionType())
                .originAccount(transferData.getOriginAccount())
                .destinationAccount(transferData.getDestinationAccount())
                .productId(transferData.getProductId())
                .productType(transferData.getProductType())
                .withCommission(transferData.getWithCommission())
                .build();
        return repo.save(saveTransaction);
    }

    @Override
    public Mono<Transaction> makePayment(TransactionDto transactionDto) {

        return TransactionUtil.findCreditById(transactionDto.getProductId()).flatMap(credit -> {
                if(transactionDto.getAmount().compareTo(credit.getMonthlyFee()) == 0){
                    return repo.countTransactionsByProductId(transactionDto.getProductId())
                            .flatMap(count -> {
                                Integer quotaNumber = count.intValue() + 1;
                                if(quotaNumber.equals(credit.getTimeLimit() + 1)){
                                    return Mono.error(new CreditPaymentAlreadyCompletedException(credit.getCreditId(), credit.getTimeLimit()));
                                }
                                PaymentData paymentData = new PaymentData(transactionDto.getAmount(), transactionDto.getTransactionDate(), transactionDto.getProductId(), quotaNumber);
                                return this.savePayment(paymentData);
                            });
                }else{
                    return  Mono.error(new CreditAmountToPayInvalidException(credit.getCreditId()));
                }
        });
    }

    public Mono<Transaction> savePayment(PaymentData paymentData){
        Transaction saveTransaction = Transaction.builder()
                .transactionDate(LocalDate.parse(paymentData.getTransactionDate(), FORMATTER))
                .amount(paymentData.getAmount())
                .transactionType(paymentData.getTransactionType())
                .productId(paymentData.getProductId())
                .productType(paymentData.getProductType())
                .quotaNumber(paymentData.getQuotaNumber()).build();

        return repo.save(saveTransaction);
    }

    @Override
    public Mono<Transaction> makeConsumption(TransactionDto transaction) {
        ConsumptionData consumptionData = new ConsumptionData(transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
               transaction.getCommerceName());

        return TransactionUtil.findCreditCardById(transaction.getProductId()).flatMap(creditCard -> {
            BigDecimal creditLimit = creditCard.getCreditLimit();
            BigDecimal remainingCredit = creditCard.getRemainingCredit();

            if(creditLimit.compareTo(transaction.getAmount()) != -1 && remainingCredit.compareTo(transaction.getAmount()) != -1){
                creditCard.setRemainingCredit(remainingCredit.subtract(transaction.getAmount()));
                return TransactionUtil.updateCreditCardLimit(creditCard)
                        .flatMap(update -> this.saveConsumption(consumptionData));
            }else{
                return Mono.error(new CreditCardWithInsuficientBalanceException(creditCard.getCreditCardId()));
            }
        });
    }

    @Override
    public Flux<Transaction> findTransactionsByProductId(String productId) {
        return repo.findAllByProductId(productId);
    }

    @Override
    public Flux<Transaction> findTransactionsByProductTypeAndProductId(String productType, String productId) {
        return repo.findAllByProductTypeAndProductId(productType, productId);
    }

    public Mono<Transaction> saveConsumption(ConsumptionData consumptionData){
        Transaction saveTransaction = Transaction.builder()
                .transactionDate(LocalDate.parse(consumptionData.getTransactionDate(), FORMATTER))
                .amount(consumptionData.getAmount())
                .transactionType(consumptionData.getTransactionType())
                .productId(consumptionData.getProductId())
                .productType(consumptionData.getProductType())
                .commerceName(consumptionData.getCommerceName()).build();

        return repo.save(saveTransaction);
    }
}

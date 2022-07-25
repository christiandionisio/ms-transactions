package com.example.mstransactions.service;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.dto.ConsumptionData;
import com.example.mstransactions.data.dto.OperationData;
import com.example.mstransactions.data.dto.PaymentData;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.error.*;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.repo.TransactionRepo;
import com.example.mstransactions.utils.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionRepo repo;

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
        OperationData operationData = new OperationData(TransactionTypeEnum.DEPOSIT.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                transaction.getOriginAccount(), transaction.getDestinationAccount());

        return TransactionUtil.findAccountById(transaction.getProductId()).flatMap(account -> {
            BigDecimal actualAmount = account.getBalance();
            account.setBalance(actualAmount.add(transaction.getAmount()));
            return TransactionUtil.updateAccountBalance(account)
                    .flatMap(update -> this.saveOperation(operationData));
        });
    }

    @Override
    public Mono<Transaction> makeWithdrawal(TransactionDto transaction) {
        OperationData operationData = new OperationData(TransactionTypeEnum.WITHDRAWAL.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                transaction.getOriginAccount(), transaction.getDestinationAccount());

        return TransactionUtil.findAccountById(transaction.getProductId()).flatMap(account -> {
            BigDecimal actualAmount = account.getBalance();
            if(actualAmount.compareTo(transaction.getAmount()) != -1){
                account.setBalance(actualAmount.subtract(transaction.getAmount()));
                return TransactionUtil.updateAccountBalance(account)
                        .flatMap(update -> this.saveOperation(operationData));
            }else{
                return Mono.error(new AccountWithInsuficientBalanceException(account.getAccountId()));
            }
        });
    }

    public Mono<Transaction> saveOperation(OperationData operationData){
        Transaction saveTransaction = Transaction.builder()
                .transactionDate(operationData.getTransactionDate())
                .amount(operationData.getAmount())
                .transactionType(operationData.getTransactionType())
                .originAccount(operationData.getOriginAccount())
                .destinationAccount(operationData.getDestinationAccount())
                .productId(operationData.getProductId())
                .productType(operationData.getProductType()).build();
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
                .transactionDate(paymentData.getTransactionDate())
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
            BigDecimal actualAmount = creditCard.getCreditLimit();
            if(actualAmount.compareTo(transaction.getAmount()) != -1){
                creditCard.setCreditLimit(actualAmount.subtract(transaction.getAmount()));
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

    public Mono<Transaction> saveConsumption(ConsumptionData consumptionData){
        Transaction saveTransaction = Transaction.builder()
                .transactionDate(consumptionData.getTransactionDate())
                .amount(consumptionData.getAmount())
                .transactionType(consumptionData.getTransactionType())
                .productId(consumptionData.getProductId())
                .productType(consumptionData.getProductType())
                .commerceName(consumptionData.getCommerceName()).build();

        return repo.save(saveTransaction);
    }
}

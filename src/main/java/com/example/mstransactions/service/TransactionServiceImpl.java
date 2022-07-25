package com.example.mstransactions.service;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.dto.OperationData;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.error.AccountNotFoundException;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
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
                .amount(operationData.getAmount()).transactionType(operationData.getTransactionType())
                .originAccount(operationData.getOriginAccount()).destinationAccount(operationData.getDestinationAccount())
                .productId(operationData.getProductId()).productType(operationData.getProductType()).build();
        return repo.save(saveTransaction);
    }
}

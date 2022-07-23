package com.example.mstransactions.service;

import com.example.mstransactions.data.dto.OperationData;
import com.example.mstransactions.data.dto.TransactionData;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

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
    public Mono<ResponseEntity<Transaction>> makeDeposit(TransactionDto transaction, final ServerHttpRequest req) {
        OperationData operationData = new OperationData(TransactionTypeEnum.DEPOSIT.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                transaction.getOriginAccount(), transaction.getDestinationAccount());

        return this.saveOperation(operationData).map( p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getTransactionId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(p));
    }

    @Override
    public Mono<Transaction> makeWithdrawal(TransactionDto transaction) {
        OperationData operationData = new OperationData(TransactionTypeEnum.WITHDRAWAL.getTransactionType(), transaction.getAmount(), transaction.getTransactionDate(), transaction.getProductId(),
                transaction.getOriginAccount(), transaction.getDestinationAccount());
        //TODO UPDATE BALANCE OF ACCOUNT
        return   this.saveOperation(operationData);
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

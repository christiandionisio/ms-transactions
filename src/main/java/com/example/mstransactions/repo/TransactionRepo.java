package com.example.mstransactions.repo;

import com.example.mstransactions.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {

}

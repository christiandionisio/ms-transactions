package com.example.mstransactions.repo;

import com.example.mstransactions.model.Transaction;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TransactionRepo Repository.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {
  Mono<Long> countTransactionsByProductId(String productId);

  Flux<Transaction> findAllByProductId(String productId);

  Flux<Transaction> findAllByProductTypeAndProductId(String productType, String productId);

  Flux<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

  Flux<Transaction> findByProductIdAndTransactionDateBetweenAndWithCommissionIsTrue(
          String productId, LocalDateTime startDate, LocalDateTime endDate);

  Flux<Transaction> findByProductTypeAndTransactionDateBetween(String productType,
                                                               LocalDateTime startDate, LocalDateTime endDate);

  Flux<Transaction> findByTransactionDateBetweenAndProductId(LocalDateTime startDate,
            LocalDateTime endDate, String productId);
}

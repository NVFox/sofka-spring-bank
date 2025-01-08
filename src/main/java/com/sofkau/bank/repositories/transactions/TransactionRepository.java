package com.sofkau.bank.repositories.transactions;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Transaction;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t JOIN t.destinationAccount d JOIN t.originAccount o " +
            "WHERE d.number = :number OR o.number = :number")
    List<Transaction> findAllByAccountNumber(UUID number);
}

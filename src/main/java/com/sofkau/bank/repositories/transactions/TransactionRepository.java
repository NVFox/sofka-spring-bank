package com.sofkau.bank.repositories.transactions;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByAccountNumber(UUID number);
}

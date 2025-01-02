package com.sofkau.bank.repositories.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}

package com.sofkau.bank.repositories.transactions;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByAccount(Account account);
}

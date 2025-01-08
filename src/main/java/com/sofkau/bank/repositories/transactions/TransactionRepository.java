package com.sofkau.bank.repositories.transactions;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Transaction;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE :number IN " +
            "(SELECT a.number FROM Account a WHERE a.id IN (t.originAccount.id, t.destinationAccount.id))")
    List<Transaction> findAllByAccountNumber(UUID number);
}

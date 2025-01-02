package com.sofkau.bank.repositories.transactions;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Transaction.Action;

public interface TransactionActionRepository extends JpaRepository<Action, Integer> {
    Optional<Action> findByName(String name);
}

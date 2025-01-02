package com.sofkau.bank.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}

package com.sofkau.bank.repositories.accounts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Client;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByNumber(UUID number);

    List<Account> findAllByClient(Client client);
}

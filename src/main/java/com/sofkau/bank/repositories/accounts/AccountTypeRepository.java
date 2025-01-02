package com.sofkau.bank.repositories.accounts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Account.Type;

public interface AccountTypeRepository extends JpaRepository<Type, Integer> {
    Optional<Type> findByName(String name);
}

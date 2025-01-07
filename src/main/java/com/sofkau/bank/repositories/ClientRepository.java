package com.sofkau.bank.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    boolean existsByUserEmail(String email);

    Optional<Client> findByUserEmail(String email);
}

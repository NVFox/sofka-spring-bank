package com.sofkau.bank.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sofkau.bank.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Query("SELECT c FROM Client c JOIN c.user u WHERE u.email = :email")
    Optional<Client> findByEmail(String email);
}

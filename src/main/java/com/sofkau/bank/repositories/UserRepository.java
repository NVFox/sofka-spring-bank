package com.sofkau.bank.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofkau.bank.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}

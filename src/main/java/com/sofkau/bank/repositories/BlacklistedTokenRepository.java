package com.sofkau.bank.repositories;

import com.sofkau.bank.entities.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Integer> {
    boolean existsByToken(String token);
}

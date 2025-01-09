package com.sofkau.bank.repositories;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("SELECT s FROM Session s WHERE s.token = :token AND s.blacklisted = FALSE")
    Optional<Session> findActiveByToken(String token);

    @Query("SELECT s FROM Session s WHERE s.client = :client AND s.blacklisted = FALSE")
    Optional<Session> findActiveByClient(Client client);
}

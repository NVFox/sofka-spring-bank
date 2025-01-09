package com.sofkau.bank.services.sessions;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;

import java.util.Optional;

public interface SessionService {
    Session createSession(Session session);
    Optional<Session> findSessionByToken(String token);
    Optional<Session> findClientSession(Client client);
    void markSessionAsBlacklisted(Session session);
}

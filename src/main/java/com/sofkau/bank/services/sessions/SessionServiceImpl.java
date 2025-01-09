package com.sofkau.bank.services.sessions;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Session createSession(Session session) {
        if (session.getClient() == null)
            throw new NotFoundException();

        if (session.getId() > 0)
            throw new AlreadyExistsException();

        return sessionRepository.save(session);
    }

    @Override
    public Optional<Session> findSessionByToken(String token) {
        return sessionRepository.findActiveByToken(token);
    }

    @Override
    public Optional<Session> findClientSession(Client client) {
        return sessionRepository.findActiveByClient(client);
    }

    @Override
    public void markSessionAsBlacklisted(Session session) {
        session.setBlacklisted(true);
        sessionRepository.save(session);
    }
}

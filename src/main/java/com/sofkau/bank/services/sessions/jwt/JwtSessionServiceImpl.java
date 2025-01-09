package com.sofkau.bank.services.sessions.jwt;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.services.sessions.SessionService;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtSessionServiceImpl implements JwtSessionService {
    private final SessionService sessionService;
    private final JwtInterpreter jwtInterpreter;

    public JwtSessionServiceImpl(SessionService sessionService, JwtInterpreter jwtInterpreter) {
        this.sessionService = sessionService;
        this.jwtInterpreter = jwtInterpreter;
    }

    @Override
    public Session getOrCreateClientSession(Client client) {
        Session session = sessionService
                .findClientSession(client)
                .orElse(new Session());

        if (session.getId() > 0)
            return session;

        String token = jwtInterpreter
                .generateAccessToken(client.getUsername());

        session.setToken(token);
        session.setClient(client);
        session.setLastAccess(LocalDateTime.now());

        Date issuedAt = jwtInterpreter.extractIssuedAtDate(token);
        session.setCreatedAt(convertDateToLocalDateTime(issuedAt));

        Date expiresAt = jwtInterpreter.extractExpirationDate(token);
        session.setExpiresAt(convertDateToLocalDateTime(expiresAt));

        session.setExpirationTime(jwtInterpreter.getExpirationTime());

        return sessionService.createSession(session);
    }

    @Override
    public void invalidateClientSession(Client client) {
        Session session = sessionService.findClientSession(client)
                .orElseThrow(NotFoundException::new);

        sessionService.markSessionAsBlacklisted(session);
    }

    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}

package com.sofkau.bank.services.sessions.jwt;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;

public interface JwtSessionService {
    Session getOrCreateClientSession(Client client);
    void invalidateClientSession(Client client);
}

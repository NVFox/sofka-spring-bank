package com.sofkau.bank.services.tokens.auth;

import com.sofkau.bank.utils.records.Session;
import com.sofkau.bank.entities.Client;

public interface JwtTokenAuthService {
    Session generateAccessTokenFrom(Client client);
    void destroyAccessToken(String token);
}

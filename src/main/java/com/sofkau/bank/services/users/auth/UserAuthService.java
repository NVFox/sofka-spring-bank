package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.entities.Client;

public interface UserAuthService {
    Client signup(Client client);
}

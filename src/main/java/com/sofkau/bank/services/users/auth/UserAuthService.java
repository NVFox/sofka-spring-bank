package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;

public interface UserAuthService {
    Client signup(Client client);
    Client login(User user);
}

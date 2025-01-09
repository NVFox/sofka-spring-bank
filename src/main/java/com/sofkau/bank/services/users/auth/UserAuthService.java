package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.utils.records.Session;
import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;

public interface UserAuthService {
    Session signup(Client client);
    Session login(User user);
    void logout(String token);
}

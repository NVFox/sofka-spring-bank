package com.sofkau.bank.services.users;

import com.sofkau.bank.entities.User;

public interface UserService {
    User createUser(User user);
    User updateUser(String email, User user);
    void deleteUserByEmail(String email);
}

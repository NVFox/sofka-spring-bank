package com.sofkau.bank.http.requests;

import com.sofkau.bank.entities.User;

public record CreateUserRequest(
        String email,
        String name,
        String password) {

    public User toUser() {
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }

}

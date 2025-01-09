package com.sofkau.bank.http.requests;

import com.sofkau.bank.entities.User;

public record LoginRequest(
        String email,
        String password) {

    public User toUser() {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }

}

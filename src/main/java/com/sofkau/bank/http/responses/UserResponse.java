package com.sofkau.bank.http.responses;

import com.sofkau.bank.entities.User;

public record UserResponse(
        String email,
        String name
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getEmail(), user.getName());
    }
}

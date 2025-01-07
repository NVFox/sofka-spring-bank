package com.sofkau.bank.http.requests;

import com.sofkau.bank.entities.Client;

public record CreateClientRequest(
        String firstName,
        String lastName,
        CreateUserRequest user) {

    public Client toClient() {
        return Client.from(user.toUser())
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

}

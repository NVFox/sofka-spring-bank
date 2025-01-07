package com.sofkau.bank.http.responses;

import com.sofkau.bank.entities.Client;

public record ClientResponse(
        String firstName,
        String lastName) {

    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getFirstName(), client.getLastName());
    }
}

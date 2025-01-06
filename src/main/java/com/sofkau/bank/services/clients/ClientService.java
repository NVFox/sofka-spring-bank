package com.sofkau.bank.services.clients;

import java.util.Optional;

import com.sofkau.bank.entities.Client;

public interface ClientService {
    Client createClient(Client client);

    Optional<Client> findClientByUserEmail(String email);
}

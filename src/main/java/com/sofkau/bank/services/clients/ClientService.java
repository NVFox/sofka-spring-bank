package com.sofkau.bank.services.clients;

import com.sofkau.bank.entities.Client;

public interface ClientService {
    Client createClient(Client client);

    Client updateClient(String email, Client client);

    Client findClientByUserEmail(String email);

    void deleteClientByEmail(String email);
}

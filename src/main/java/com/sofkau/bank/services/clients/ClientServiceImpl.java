package com.sofkau.bank.services.clients;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        if (client.getUser() == null)
            throw new NotFoundException();

        if (client.getId() > 0)
            throw new AlreadyExistsException();

        return clientRepository.save(client);
    }

    public Optional<Client> findClientByUserEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}

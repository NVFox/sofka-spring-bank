package com.sofkau.bank.services.clients;

import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.ClientRepository;
import com.sofkau.bank.services.users.UserService;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;

    public ClientServiceImpl(ClientRepository clientRepository, UserService userService) {
        this.clientRepository = clientRepository;
        this.userService = userService;
    }

    public Client createClient(Client client) {
        if (client.getId() > 0)
            throw new AlreadyExistsException();

        client.setUser(userService
                .createUser(client.getUser()));

        return clientRepository.save(client);
    }

    public Client findClientByUserEmail(String email) {
        return clientRepository.findByUserEmail(email)
                .orElseThrow(NotFoundException::new);
    }
}

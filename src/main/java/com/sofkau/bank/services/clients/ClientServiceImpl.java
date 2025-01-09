package com.sofkau.bank.services.clients;

import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
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
        User user = client.getUser();

        if (user == null)
            throw new NotFoundException();

        if (clientRepository.existsByUserEmail(user.getEmail()))
            throw new AlreadyExistsException();

        client.setUser(userService.createUser(user));

        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(String email, Client client) {
        Client stored = findClientByUserEmail(email);

        if (client.getFirstName() != null)
            stored.setFirstName(client.getFirstName());

        if (client.getLastName() != null)
            stored.setLastName(client.getLastName());

        stored.setUser(userService.updateUser(email, client.getUser()));

        return clientRepository.save(stored);
    }

    public Client findClientByUserEmail(String email) {
        return clientRepository.findByUserEmail(email)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void deleteClientByEmail(String email) {
        clientRepository.deleteByUserEmail(email);
        userService.deleteUserByEmail(email);
    }
}

package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.services.clients.ClientService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService, UserDetailsService {
    private final ClientService clientService;

    public UserAuthServiceImpl(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Client signup(Client client) {
        return clientService.createClient(client);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return clientService.findClientByUserEmail(email);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("User email has not been found.", e);
        }
    }
}
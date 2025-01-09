package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.services.clients.ClientService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService, UserDetailsService {
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    public UserAuthServiceImpl(ClientService clientService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Client signup(Client client) {
        User user = client.getUser();

        user.setPassword(passwordEncoder
                .encode(user.getPassword()));

        return clientService.createClient(client);
    }

    @Override
    public Client login(User user) {
        Client client = clientService
                .findClientByUserEmail(user.getEmail());

        if (!passwordEncoder.matches(user.getPassword(), client.getPassword()))
            throw new BadCredentialsException("Wrong password or email");

        return client;
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
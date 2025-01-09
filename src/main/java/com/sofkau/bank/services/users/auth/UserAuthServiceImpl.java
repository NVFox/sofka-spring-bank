package com.sofkau.bank.services.users.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Session;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.services.clients.ClientService;
import com.sofkau.bank.services.sessions.jwt.JwtSessionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService, UserDetailsService {
    private final ClientService clientService;
    private final JwtSessionService sessionService;

    private final PasswordEncoder passwordEncoder;

    public UserAuthServiceImpl(ClientService clientService, JwtSessionService sessionService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Session signup(Client client) {
        User user = client.getUser();

        user.setPassword(passwordEncoder
                .encode(user.getPassword()));

        return sessionService
                .getOrCreateClientSession(clientService.createClient(client));
    }

    @Override
    public Session login(User user) {
        Client client = clientService
                .findClientByUserEmail(user.getEmail());

        if (!passwordEncoder.matches(user.getPassword(), client.getPassword()))
            throw new BadCredentialsException("Wrong password or email");

        return sessionService
                .getOrCreateClientSession(client);
    }

    @Override
    public void logout(Client client) {
        sessionService.invalidateClientSession(client);
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
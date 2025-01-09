package com.sofkau.bank.controllers;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.http.requests.CreateClientRequest;
import com.sofkau.bank.http.responses.ClientResponse;
import com.sofkau.bank.services.clients.ClientService;
import com.sofkau.bank.services.tokens.BlacklistedTokenService;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final BlacklistedTokenService blacklistedTokenService;

    public ClientController(ClientService clientService, BlacklistedTokenService blacklistedTokenService) {
        this.clientService = clientService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @GetMapping
    public ClientResponse getClient(@AuthenticationPrincipal Client client) {
        return ClientResponse.from(client);
    }

    @PutMapping
    public ClientResponse updateClient(
            @RequestBody CreateClientRequest clientRequest,
            @AuthenticationPrincipal Client client,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        String email = client.getUsername();
        Client toUpdate = clientRequest.toClient();

        invalidateTokenFromHeader(auth);

        return ClientResponse.from(clientService.updateClient(email, toUpdate));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(
            @AuthenticationPrincipal Client client,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        invalidateTokenFromHeader(auth);
        clientService.deleteClientByEmail(client.getUsername());
    }

    private void invalidateTokenFromHeader(String authHeader) {
        String token = JwtInterpreter.extractFromHeader(authHeader);
        blacklistedTokenService.addToBlacklist(token);
    }
}

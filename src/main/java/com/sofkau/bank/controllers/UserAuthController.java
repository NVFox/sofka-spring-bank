package com.sofkau.bank.controllers;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.http.requests.CreateClientRequest;
import com.sofkau.bank.http.responses.ClientResponse;
import com.sofkau.bank.services.users.auth.UserAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/signup")
    public ClientResponse signup(CreateClientRequest clientRequest) {
        Client client = clientRequest.toClient();
        return ClientResponse.from(userAuthService.signup(client));
    }
}

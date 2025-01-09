package com.sofkau.bank.controllers;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.http.requests.CreateClientRequest;
import com.sofkau.bank.http.requests.LoginRequest;
import com.sofkau.bank.http.responses.ClientResponse;
import com.sofkau.bank.http.responses.LoginResponse;
import com.sofkau.bank.services.users.auth.UserAuthService;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtInterpreter jwtInterpreter;

    public UserAuthController(UserAuthService userAuthService, JwtInterpreter jwtInterpreter) {
        this.userAuthService = userAuthService;
        this.jwtInterpreter = jwtInterpreter;
    }

    @PostMapping("/signup")
    public ClientResponse signup(@RequestBody CreateClientRequest clientRequest) {
        Client client = clientRequest.toClient();
        return ClientResponse.from(userAuthService.signup(client));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Client client = userAuthService.login(loginRequest.toUser());

        String token = jwtInterpreter.generateToken(client.getUsername());
        long expiresIn = jwtInterpreter.getExpirationTime();

        return new LoginResponse(token, expiresIn);
    }
}

package com.sofkau.bank.controllers;

import com.sofkau.bank.utils.records.Session;
import com.sofkau.bank.http.requests.CreateClientRequest;
import com.sofkau.bank.http.requests.LoginRequest;
import com.sofkau.bank.http.responses.LoginResponse;
import com.sofkau.bank.services.users.auth.UserAuthService;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse signup(@RequestBody CreateClientRequest clientRequest) {
        Session session = userAuthService.signup(clientRequest.toClient());
        return new LoginResponse(session.token(), session.expirationTime());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Session session = userAuthService.login(loginRequest.toUser());
        return new LoginResponse(session.token(), session.expirationTime());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        String token = JwtInterpreter.extractFromHeader(auth);
        userAuthService.logout(token);
    }
}

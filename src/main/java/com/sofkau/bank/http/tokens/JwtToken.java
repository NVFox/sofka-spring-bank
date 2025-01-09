package com.sofkau.bank.http.tokens;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtToken extends UsernamePasswordAuthenticationToken {
    private JwtToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public static JwtToken create(String userEmail, String jwt) {
        return new JwtToken(userEmail, jwt);
    }
}

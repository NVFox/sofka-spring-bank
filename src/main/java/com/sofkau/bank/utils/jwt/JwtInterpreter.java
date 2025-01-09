package com.sofkau.bank.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtInterpreter {
    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    public static String extractFromHeader(String header) {
        String authHeader = header != null ? header.trim() : null;

        if (authHeader == null ||
                !StringUtils.startsWithIgnoreCase(authHeader, "Bearer"))
            return null;

        String jwtToken = authHeader.substring(7).trim();

        if (jwtToken.equalsIgnoreCase("Bearer"))
            throw new BadCredentialsException("Empty bearer authentication token");

        return jwtToken;
    }

    public String generateAccessToken(String userEmail) {
        return Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(generateKey())
                .compact();
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUserEmail(token).equals(userDetails.getUsername());
    }

    public Date extractIssuedAtDate(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractClaims(token));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey generateKey() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
}

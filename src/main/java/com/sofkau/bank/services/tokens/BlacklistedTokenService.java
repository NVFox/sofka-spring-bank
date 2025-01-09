package com.sofkau.bank.services.tokens;

public interface BlacklistedTokenService {
    boolean isBlacklisted(String token);
    void addToBlacklist(String token);
}

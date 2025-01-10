package com.sofkau.bank.services.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.services.tokens.BlacklistedTokenService;
import com.sofkau.bank.services.tokens.auth.JwtTokenAuthServiceImpl;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import com.sofkau.bank.utils.records.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenAuthServiceTest {
    @Mock
    private JwtInterpreter jwtInterpreter;

    @Mock
    private BlacklistedTokenService blacklistedTokenService;

    @InjectMocks
    private JwtTokenAuthServiceImpl jwtTokenAuthService;

    @Test
    void whenGenerateAccessTokenThenReturnString() {
        Client sample = new Client();
        sample.setUser(User.builder()
                .email("any@mail.com")
                .build());

        when(jwtInterpreter.generateAccessToken(anyString()))
                .thenReturn("tok3n");

        when(jwtInterpreter.getExpirationTime())
                .thenReturn(1800000L);

        Session result = assertDoesNotThrow(() -> jwtTokenAuthService.generateAccessTokenFrom(sample));

        assertNotNull(result);
        assertEquals("tok3n", result.token());
        assertEquals(1800000L, result.expirationTime());
    }

    @Test
    void whenDestroyAccessTokenThenReturnsNothing() {
        doNothing().when(blacklistedTokenService)
                .addToBlacklist(anyString());

        assertDoesNotThrow(() -> jwtTokenAuthService.destroyAccessToken("tok3n"));

        verify(blacklistedTokenService, times(1))
                .addToBlacklist(anyString());
    }
}

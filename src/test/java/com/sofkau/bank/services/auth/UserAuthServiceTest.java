package com.sofkau.bank.services.auth;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.services.clients.ClientService;
import com.sofkau.bank.services.tokens.auth.JwtTokenAuthService;
import com.sofkau.bank.services.users.auth.UserAuthServiceImpl;
import com.sofkau.bank.utils.records.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceTest {
    @Mock
    private ClientService clientService;

    @Mock
    private JwtTokenAuthService jwtTokenAuthService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    @Test
    void whenSignupIsSuccessfulThenReturnsSession() {
        User sampleUser = spy(User.builder()
                .password("p4sS")
                .build());

        Client sampleClient = Client.from(sampleUser).build();

        Session sampleSession = new Session("tok3n", 1800000L);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encrypted");

        when(clientService.createClient(any(Client.class)))
                .thenReturn(sampleClient);

        when(jwtTokenAuthService.generateAccessTokenFrom(any(Client.class)))
                .thenReturn(sampleSession);

        Session result = assertDoesNotThrow(() -> userAuthService.signup(sampleClient));

        verify(sampleUser, times(1))
                .setPassword(eq("encrypted"));

        verify(clientService, times(1))
                .createClient(any(Client.class));

        assertNotNull(result);
        assertEquals(sampleSession, result);
    }

    @Test
    void whenLoginIsSuccessfulThenReturnsSession() {
        User logUser = User.builder()
                .email("any@mail.com")
                .password("p4sS")
                .build();

        Client sampleClient = Client.from(logUser).build();

        Session sampleSession = new Session("tok3n", 1800000L);

        when(clientService.findClientByUserEmail(anyString()))
                .thenReturn(sampleClient);

        when(passwordEncoder.matches(eq(logUser.getPassword()), eq(sampleClient.getPassword())))
                .thenReturn(logUser.getPassword().equals(sampleClient.getPassword()));

        when(jwtTokenAuthService.generateAccessTokenFrom(any(Client.class)))
                .thenReturn(sampleSession);

        Session result = assertDoesNotThrow(() -> userAuthService.login(logUser));

        verify(clientService, times(1))
                .findClientByUserEmail(eq(logUser.getEmail()));

        verify(passwordEncoder, times(1))
                .matches(eq(logUser.getPassword()), eq(sampleClient.getPassword()));

        assertNotNull(result);
        assertEquals(sampleSession, result);
    }

    @Test
    void whenLoginHasCredentialsThatDoNotMatchThenThrowsException() {
        User logUser = User.builder()
                .email("any@mail.com")
                .password("p4sS")
                .build();

        User sampleUser = User.builder()
                .email(logUser.getEmail())
                .password("an0th3r")
                .build();

        Client sampleClient = Client.from(sampleUser).build();

        when(clientService.findClientByUserEmail(anyString()))
                .thenReturn(sampleClient);

        when(passwordEncoder.matches(eq(logUser.getPassword()), eq(sampleClient.getPassword())))
                .thenReturn(logUser.getPassword().equals(sampleClient.getPassword()));

        assertThrows(BadCredentialsException.class, () -> userAuthService.login(logUser));

        verify(clientService, times(1))
                .findClientByUserEmail(eq(logUser.getEmail()));

        verify(passwordEncoder, times(1))
                .matches(eq(logUser.getPassword()), eq(sampleClient.getPassword()));

        verify(jwtTokenAuthService, never())
                .generateAccessTokenFrom(any(Client.class));
    }

    @Test
    void whenLogoutIsSuccessfulThenReturnsNothing() {
        doNothing().when(jwtTokenAuthService)
                .destroyAccessToken(anyString());

        assertDoesNotThrow(() -> userAuthService.logout("tok3n"));

        verify(jwtTokenAuthService, times(1))
                .destroyAccessToken(eq("tok3n"));
    }

    @Test
    void whenLoadUserByUsernameFindsClientThenReturnsUserDetails() {
        when(clientService.findClientByUserEmail(anyString()))
                .thenReturn(new Client());

        UserDetails result = assertDoesNotThrow(() -> userAuthService
                .loadUserByUsername("any@mail.com"));

        assertNotNull(result);
    }

    @Test
    void whenLoadByUsernameDoesNotFindClientThenThrowsException() {
        when(clientService.findClientByUserEmail(anyString()))
                .thenThrow(new NotFoundException());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userAuthService
                .loadUserByUsername("any@mail.com"));

        assertInstanceOf(NotFoundException.class, exception.getCause());
    }
}

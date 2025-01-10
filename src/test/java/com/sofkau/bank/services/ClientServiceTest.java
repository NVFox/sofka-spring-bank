package com.sofkau.bank.services;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.ClientRepository;
import com.sofkau.bank.services.clients.ClientServiceImpl;
import com.sofkau.bank.services.users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void whenCreateClientHasANewClientThenReturnClient() {
        // Arrange
        User user = User.builder()
                .email("any@mail.com")
                .build();

        Client client = Client.from(user)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(clientRepository.existsByUserEmail(anyString()))
                .thenReturn(false);

        when(userService.createUser(any(User.class)))
                .thenReturn(user);

        when(clientRepository.save(any(Client.class)))
                .thenReturn(client);

        // Act & assert
        assertDoesNotThrow(() -> clientService.createClient(client));

        verify(clientRepository, times(1))
                .existsByUserEmail(eq(user.getEmail()));

        verify(userService, times(1))
                .createUser(eq(user));

        verify(clientRepository, times(1))
                .save(any(Client.class));
    }

    @Test
    void whenCreateClientMissesClientThenThrowException() {
        // Arrange, act & assert
        assertThrows(NotFoundException.class, () ->
                clientService.createClient(new Client()));

        verify(clientRepository, never())
                .existsByUserEmail(anyString());

        verify(userService, never()).createUser(any(User.class));

        verify(clientRepository, never()).save(any());
    }

    @Test
    void whenCreateClientHasAlreadyCreatedClientThenThrowException() {
        // Arrange
        User user = User.builder()
                .email("any@mail.com")
                .build();

        Client client = Client.from(user)
                .build();

        when(clientRepository.existsByUserEmail(anyString()))
                .thenReturn(true);

        // Act & assert
        assertThrows(AlreadyExistsException.class, () ->
            clientService.createClient(client));

        verify(clientRepository, times(1))
                .existsByUserEmail(anyString());

        verify(userService, never()).createUser(any(User.class));

        verify(clientRepository, never()).save(any());
    }

    @Test
    void whenUpdateClientHasDataClientGetsUpdatedThenReturnClient() {
        // Arrange
        Client clientStored = spy(Client.from(new User())
                .build());

        Client clientForUpdate = Client.from(new User())
                .firstName("John")
                .lastName("Doe")
                .build();

        when(clientRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.of(clientStored));

        when(clientRepository.save(any(Client.class)))
                .thenReturn(clientStored);

        when(userService.updateUser(anyString(), any(User.class)))
                .thenReturn(new User());

        // Act & assert
        Client result = assertDoesNotThrow(() ->
                clientService.updateClient("any@mail.com", clientForUpdate));

        verify(clientStored, times(1))
                .setFirstName(eq(clientForUpdate.getFirstName()));

        verify(clientStored, times(1))
                .setLastName(eq(clientForUpdate.getLastName()));

        verify(clientStored, times(1))
                .setUser(any(User.class));

        verify(clientRepository, times(1))
                .findByUserEmail(anyString());

        verify(clientRepository, times(1))
                .save(eq(clientStored));

        assertEquals(clientForUpdate.getLastName(), result.getLastName());
        assertEquals(clientForUpdate.getLastName(), result.getLastName());
        assertNotNull(result.getUser());
    }

    @Test
    void whenUpdateClientDoesNotHaveDataClientDoesNotGetUpdatedThenReturnClient() {
        // Arrange
        User userStored = User.builder()
                .email("any@mail.com")
                .password("")
                .build();

        Client clientStored = spy(Client.from(userStored)
                .firstName("John")
                .lastName("Doe")
                .build());

        Client clientForUpdate = Client.from(new User())
                .build();

        when(clientRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.of(clientStored));

        when(clientRepository.save(any(Client.class)))
                .thenReturn(clientStored);

        when(userService.updateUser(anyString(), any(User.class)))
                .thenReturn(new User());

        // Act & assert
        Client result = assertDoesNotThrow(() ->
                clientService.updateClient(userStored.getEmail(), clientForUpdate));

        verify(clientStored, never()).setFirstName(anyString());

        verify(clientStored, never()).setLastName(anyString());

        verify(clientStored, times(1))
                .setUser(any(User.class));

        verify(userService, times(1))
                .updateUser(anyString(), any(User.class));

        verify(clientRepository, times(1))
                .save(eq(clientStored));

        assertEquals(clientStored.getLastName(), result.getLastName());
        assertEquals(clientStored.getLastName(), result.getLastName());
        assertNotNull(result.getUser());
    }

    @Test
    void whenUpdateUserAndEmailNotFoundThenThrowException() {
        // Arrange
        when(clientRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.empty());

        // Act & assert
        assertThrows(NotFoundException.class, () -> clientService.updateClient("", new Client()));
    }

    @Test
    void whenFindClientByUserEmailFindsAClientAndReturnsClient() {
        when(clientRepository.findByUserEmail(anyString()))
                .thenReturn(Optional.of(new Client()));

        assertDoesNotThrow(() -> clientService.findClientByUserEmail("any@mail.com"));
    }

    @Test
    void whenDeleteUserThenDoNothing() {
        // Arrange
        String email = "any@gmail.com";

        doNothing().when(clientRepository)
                .deleteByUserEmail(anyString());

        assertDoesNotThrow(() -> clientService.deleteClientByEmail(email));

        verify(clientRepository, times(1))
                .deleteByUserEmail(eq(email));

        verify(userService, times(1))
                .deleteUserByEmail(eq(email));
    }
}

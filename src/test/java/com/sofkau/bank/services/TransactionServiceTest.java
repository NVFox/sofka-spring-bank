package com.sofkau.bank.services;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.exceptions.NotFoundException;
import com.sofkau.bank.repositories.transactions.TransactionActionRepository;
import com.sofkau.bank.repositories.transactions.TransactionRepository;
import com.sofkau.bank.services.transactions.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionActionRepository transactionActionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void whenCreateTransactionGetsTransactionCreatedThenReturnTransaction() {
        // Arrange
        Transaction sample = new Transaction();
        sample.setOriginAccount(new Account());
        sample.setDestinationAccount(new Account());

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(sample);

        // Act & assert
        Transaction result = assertDoesNotThrow(() -> transactionService.createTransaction(sample));

        verify(transactionRepository, times(1))
                .save(eq(sample));

        assertNotNull(result);
    }

    @Test
    void whenCreateTransactionDoesNotHaveAccountsThenThrowException() {
        // Arrange
        Transaction sample = new Transaction();

        // Act & assert
        assertThrows(NotFoundException.class, () -> transactionService.createTransaction(sample));

        verify(transactionRepository, never())
                .save(eq(sample));
    }

    @Test
    void whenCreateTransactionHasIdGreaterThanZeroThenThrowException() {
        // Arrange
        Transaction sample = new Transaction();
        sample.setId(1);
        sample.setDestinationAccount(new Account());

        // Act & assert
        assertThrows(AlreadyExistsException.class, () -> transactionService.createTransaction(sample));

        verify(transactionRepository, never())
                .save(eq(sample));
    }

    @Test
    void whenFindTransactionActionByNameFindsActionThenReturnAction() {
        // Arrange
        Transaction.Action.Name sampleName = Transaction.Action.Name.DEPOSIT;

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(sampleName.name());

        when(transactionActionRepository.findByName(anyString()))
                .thenReturn(Optional.of(sampleAction));

        // Act & assert
        Transaction.Action result = assertDoesNotThrow(() -> transactionService
                .findTransactionActionByName(sampleName));

        assertNotNull(result);
        assertEquals(sampleAction, result);
    }

    @Test
    void whenFindTransactionActionByNameDoesNotFindThenThrowException() {
        Transaction.Action.Name sampleName = Transaction.Action.Name.DEPOSIT;

        when(transactionActionRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService
                .findTransactionActionByName(sampleName));
    }

    @Test
    void whenFindTransactionsByAccountNumberFindsTransactionsReturnList() {
        List<Transaction> sample = List.of(new Transaction(), new Transaction());

        when(transactionRepository.findAllByAccountNumber(any(UUID.class)))
                .thenReturn(sample);

        List<Transaction> result = assertDoesNotThrow(() -> transactionService
                .findTransactionsByAccountNumber(UUID.randomUUID()));

        assertNotNull(result);
        assertEquals(sample.size(), result.size());
    }
}

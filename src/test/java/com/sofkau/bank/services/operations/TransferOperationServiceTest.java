package com.sofkau.bank.services.operations;

import com.sofkau.bank.commands.TransferCommand;
import com.sofkau.bank.constants.OperationTypes;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.exceptions.account.NegativeAmountException;
import com.sofkau.bank.exceptions.account.NotEnoughFundsException;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.transactions.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class TransferOperationServiceTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferOperationService transferOperationService;

    @Test
    void whenProcessIsSuccessfulThenReturnTransaction() {
        // Arrange
        BigDecimal sampleAmount = BigDecimal.valueOf(10000);

        Account sampleOriginAccount = spy(new Account());
        sampleOriginAccount.setNumber(UUID.randomUUID());
        sampleOriginAccount.setBalance(sampleAmount.add(BigDecimal.valueOf(10000)));

        Account sampleDestinationAccount = new Account();
        sampleDestinationAccount.setNumber(UUID.randomUUID());
        sampleDestinationAccount.setBalance(BigDecimal.ZERO);

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.TRANSFER);

        TransferCommand sampleCommand = TransferCommand.on(sampleOriginAccount, sampleDestinationAccount)
                .with(sampleAmount);

        BigDecimal expectedOriginBalance = sampleOriginAccount.getBalance()
                .subtract(sampleAmount);

        BigDecimal expectedDestinationBalance = sampleDestinationAccount.getBalance()
                .add(sampleAmount);

        ArgumentCaptor<Transaction> transactionGenerated = ArgumentCaptor.forClass(Transaction.class);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.TRANSFER))
                .thenReturn(sampleAction);

        doNothing().when(accountService).updateAccount(any(UUID.class), any(Account.class));

        when(transactionService.createTransaction(transactionGenerated.capture()))
                .thenAnswer(mock -> mock.getArguments()[0]);

        // Act & assert
        assertDoesNotThrow(() -> transferOperationService.process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .transferFunds(eq(sampleAmount), eq(sampleDestinationAccount));

        verify(accountService, times(2))
                .updateAccount(any(UUID.class), any(Account.class));

        assertEquals(expectedDestinationBalance, sampleDestinationAccount.getBalance());
        assertEquals(expectedOriginBalance, sampleOriginAccount.getBalance());

        Transaction result = transactionGenerated.getValue();

        assertNotNull(result);
        assertEquals(sampleDestinationAccount, result.getDestinationAccount());
        assertEquals(sampleOriginAccount, result.getOriginAccount());
        assertEquals(sampleAction, result.getAction());
        assertEquals(sampleAmount, result.getAmount());
    }

    @Test
    void whenProcessCanNotUpdateBalanceByNegativeAmount() {
        BigDecimal sampleAmount = BigDecimal.valueOf(-10000);

        Account sampleOriginAccount = spy(new Account());
        Account sampleDestinationAccount = new Account();

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.TRANSFER);

        TransferCommand sampleCommand = TransferCommand.on(sampleOriginAccount, sampleDestinationAccount)
                .with(sampleAmount);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.TRANSFER))
                .thenReturn(sampleAction);

        assertThrows(NegativeAmountException.class, () -> transferOperationService
                .process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .transferFunds(eq(sampleAmount), eq(sampleDestinationAccount));

        verify(accountService, never())
                .updateAccount(any(), any());

        verify(transactionService, never())
                .createTransaction(any());
    }

    @Test
    void whenProcessCanNotUpdateBalanceByNotEnoughFunds() {
        BigDecimal sampleAmount = BigDecimal.valueOf(10000);

        Account sampleOriginAccount = spy(new Account());
        sampleOriginAccount.setNumber(UUID.randomUUID());
        sampleOriginAccount.setBalance(BigDecimal.ZERO);

        Account sampleDestinationAccount = new Account();
        sampleDestinationAccount.setNumber(UUID.randomUUID());
        sampleDestinationAccount.setBalance(BigDecimal.ZERO);

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.TRANSFER);

        TransferCommand sampleCommand = TransferCommand.on(sampleOriginAccount, sampleDestinationAccount)
                .with(sampleAmount);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.TRANSFER))
                .thenReturn(sampleAction);

        assertThrows(NotEnoughFundsException.class, () -> transferOperationService
                .process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .transferFunds(eq(sampleAmount), eq(sampleDestinationAccount));

        verify(accountService, never())
                .updateAccount(any(), any());

        verify(transactionService, never())
                .createTransaction(any());
    }
}

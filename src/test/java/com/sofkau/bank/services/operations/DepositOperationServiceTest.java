package com.sofkau.bank.services.operations;

import com.sofkau.bank.commands.DepositCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositOperationServiceTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private DepositOperationService depositOperationService;

    @Test
    void whenProcessIsSuccessfulThenReturnTransaction() {
        BigDecimal sampleAmount = BigDecimal.valueOf(10000);

        Account sampleDestinationAccount = spy(new Account());
        sampleDestinationAccount.setNumber(UUID.randomUUID());
        sampleDestinationAccount.setBalance(BigDecimal.ZERO);

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.DEPOSIT);

        DepositCommand sampleCommand = DepositCommand.on(sampleDestinationAccount)
                .with(sampleAmount);

        BigDecimal expectedAccountBalance = sampleDestinationAccount.getBalance()
                .add(sampleAmount);

        ArgumentCaptor<Transaction> transactionGenerated = ArgumentCaptor.forClass(Transaction.class);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.DEPOSIT))
                .thenReturn(sampleAction);

        doNothing().when(accountService).updateAccount(any(UUID.class), any(Account.class));

        when(transactionService.createTransaction(transactionGenerated.capture()))
                .thenAnswer(mock -> mock.getArguments()[0]);

        assertDoesNotThrow(() -> depositOperationService.process(sampleCommand));

        verify(sampleDestinationAccount, times(1))
                .depositFunds(eq(sampleAmount));

        assertEquals(expectedAccountBalance, sampleDestinationAccount.getBalance());

        Transaction result = transactionGenerated.getValue();

        assertNotNull(result);
        assertNull(result.getOriginAccount());
        assertEquals(sampleDestinationAccount, result.getDestinationAccount());
        assertEquals(sampleAction, result.getAction());
        assertEquals(sampleAmount, result.getAmount());
    }

    @Test
    void whenProcessCanNotUpdateBalanceByNegativeAmount() {
        BigDecimal sampleAmount = BigDecimal.valueOf(-10000);

        Account sampleDestinationAccount = spy(new Account());

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.DEPOSIT);

        DepositCommand sampleCommand = DepositCommand.on(sampleDestinationAccount)
                .with(sampleAmount);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.DEPOSIT))
                .thenReturn(sampleAction);

        assertThrows(NegativeAmountException.class, () -> depositOperationService
                .process(sampleCommand));

        verify(sampleDestinationAccount, times(1))
                .depositFunds(eq(sampleAmount));

        verify(accountService, never())
                .updateAccount(any(), any());

        verify(transactionService, never())
                .createTransaction(any());
    }
}

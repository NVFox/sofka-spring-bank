package com.sofkau.bank.services.operations;

import com.sofkau.bank.commands.DepositCommand;
import com.sofkau.bank.commands.WithdrawalCommand;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class WithdrawalOperationServiceTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private WithdrawalOperationService withdrawalOperationService;

    @Test
    void whenProcessIsSuccessfulThenReturnTransaction() {
        BigDecimal sampleAmount = BigDecimal.valueOf(10000);

        Account sampleOriginAccount = spy(new Account());
        sampleOriginAccount.setNumber(UUID.randomUUID());
        sampleOriginAccount.setBalance(sampleAmount.add(BigDecimal.valueOf(10000)));

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.WITHDRAWAL);

        WithdrawalCommand sampleCommand = WithdrawalCommand.on(sampleOriginAccount)
                .with(sampleAmount);

        BigDecimal expectedAccountBalance = sampleOriginAccount.getBalance()
                .subtract(sampleAmount);

        ArgumentCaptor<Transaction> transactionGenerated = ArgumentCaptor.forClass(Transaction.class);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.WITHDRAWAL))
                .thenReturn(sampleAction);

        doNothing().when(accountService).updateAccount(any(UUID.class), any(Account.class));

        when(transactionService.createTransaction(transactionGenerated.capture()))
                .thenAnswer(mock -> mock.getArguments()[0]);

        assertDoesNotThrow(() -> withdrawalOperationService.process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .withdrawFunds(eq(sampleAmount));

        assertEquals(expectedAccountBalance, sampleOriginAccount.getBalance());

        Transaction result = transactionGenerated.getValue();

        assertNotNull(result);
        assertNull(result.getDestinationAccount());
        assertEquals(sampleOriginAccount, result.getOriginAccount());
        assertEquals(sampleAction, result.getAction());
        assertEquals(sampleAmount, result.getAmount());
    }

    @Test
    void whenProcessCanNotUpdateBalanceByNegativeAmount() {
        BigDecimal sampleAmount = BigDecimal.valueOf(-10000);

        Account sampleOriginAccount = spy(new Account());

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.WITHDRAWAL);

        WithdrawalCommand sampleCommand = WithdrawalCommand.on(sampleOriginAccount)
                .with(sampleAmount);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.WITHDRAWAL))
                .thenReturn(sampleAction);

        assertThrows(NegativeAmountException.class, () -> withdrawalOperationService
                .process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .withdrawFunds(eq(sampleAmount));

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

        Transaction.Action sampleAction = new Transaction.Action();
        sampleAction.setName(OperationTypes.WITHDRAWAL);

        WithdrawalCommand sampleCommand = WithdrawalCommand.on(sampleOriginAccount)
                .with(sampleAmount);

        when(transactionService.findTransactionActionByName(Transaction.Action.Name.WITHDRAWAL))
                .thenReturn(sampleAction);

        assertThrows(NotEnoughFundsException.class, () -> withdrawalOperationService
                .process(sampleCommand));

        verify(sampleOriginAccount, times(1))
                .withdrawFunds(eq(sampleAmount));

        verify(accountService, never())
                .updateAccount(any(), any());

        verify(transactionService, never())
                .createTransaction(any());
    }
}

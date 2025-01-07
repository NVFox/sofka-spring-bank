package com.sofkau.bank.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sofkau.bank.commands.WithdrawalCommand;
import com.sofkau.bank.constants.OperationTypes;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.transactions.TransactionService;

@Service(OperationTypes.WITHDRAWAL)
public class WithdrawalOperationService implements OperationService<WithdrawalCommand> {
    private TransactionService transactionService;
    private AccountService accountService;

    public WithdrawalOperationService(
            TransactionService transactionService,
            AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    public void process(WithdrawalCommand command) {
        Account origin = command.getOrigin();
        BigDecimal amount = command.getAmount();

        Action withdrawal = transactionService
                .findTransactionActionByName(Action.Name.WITHDRAWAL);

        Transaction transaction = Transaction.on(origin)
                .by(withdrawal, amount);

        origin.withdrawFunds(amount);

        accountService
                .updateAccount(origin.getNumber(), origin);

        transactionService.createTransaction(transaction);
    }
}

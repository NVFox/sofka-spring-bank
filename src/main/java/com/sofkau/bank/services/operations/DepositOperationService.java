package com.sofkau.bank.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sofkau.bank.commands.DepositCommand;
import com.sofkau.bank.constants.OperationTypes;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.transactions.TransactionService;

@Service(OperationTypes.DEPOSIT)
public class DepositOperationService implements OperationService<DepositCommand> {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public DepositOperationService(
            TransactionService transactionService,
            AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    public void process(DepositCommand command) {
        Account destination = command.getDestination();
        BigDecimal amount = command.getAmount();

        Action deposit = transactionService
                .findTransactionActionByName(Action.Name.DEPOSIT);

        Transaction transaction = Transaction.on(destination)
                .by(deposit, amount);

        destination.depositFunds(amount);

        accountService
                .updateAccount(destination.getNumber(), destination);

        transactionService.createTransaction(transaction);
    }
}

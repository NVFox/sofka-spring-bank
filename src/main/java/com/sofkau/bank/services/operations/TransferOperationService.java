package com.sofkau.bank.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sofkau.bank.commands.TransferCommand;
import com.sofkau.bank.constants.OperationTypes;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.transactions.TransactionService;

@Service(OperationTypes.TRANSFER)
public class TransferOperationService implements OperationService<TransferCommand> {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransferOperationService(
            TransactionService transactionService,
            AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    public Transaction process(TransferCommand command) {
        Account origin = command.getOrigin();
        Account destination = command.getDestination();
        BigDecimal amount = command.getAmount();

        Action transfer = transactionService
                .findTransactionActionByName(Action.Name.TRANSFER);

        Transaction transaction = Transaction.on(origin, destination)
                .by(transfer, amount);

        origin.transferFunds(amount, destination);

        accountService.updateAccount(origin.getNumber(), origin);
        accountService.updateAccount(destination.getNumber(), destination);

        return transactionService.createTransaction(transaction);
    }
}

package com.sofkau.bank.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sofkau.bank.commands.TransferCommand;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.transactions.TransactionService;

@Service
public class TransferOperationService implements OperationService<TransferCommand> {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransferOperationService(
            TransactionService transactionService,
            AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    public void process(TransferCommand command) {
        Account origin = command.getOrigin();
        Account destination = command.getDestination();
        BigDecimal amount = command.getAmount();

        Action sentTransfer = transactionService
                .findTransactionActionByName(Action.Name.SENT_TRANSFER);

        Action receivedTransfer = transactionService
                .findTransactionActionByName(Action.Name.RECEIVED_TRANSFER);

        Transaction transactionFromOrigin = Transaction.on(origin)
                .by(sentTransfer, amount);

        Transaction transactionToDestination = Transaction.on(destination)
                .by(receivedTransfer, amount);

        origin.transferFunds(amount, destination);

        accountService
                .updateAccount(origin.getNumber(), origin);

        accountService
                .updateAccount(destination.getNumber(), destination);

        transactionService.createTransaction(transactionFromOrigin);
        transactionService.createTransaction(transactionToDestination);
    }
}

package com.sofkau.bank.controllers;

import java.util.UUID;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.exceptions.UnauthorizedAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sofkau.bank.commands.DepositCommand;
import com.sofkau.bank.commands.TransferCommand;
import com.sofkau.bank.commands.WithdrawalCommand;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.factories.OperationServiceFactory;
import com.sofkau.bank.http.requests.DepositRequest;
import com.sofkau.bank.http.requests.TransferRequest;
import com.sofkau.bank.http.requests.WithdrawalRequest;
import com.sofkau.bank.http.responses.TransactionResponse;
import com.sofkau.bank.services.accounts.AccountService;
import com.sofkau.bank.services.operations.OperationService;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final OperationServiceFactory operations;
    private final AccountService accountService;

    public OperationController(
            OperationServiceFactory operations,
            AccountService accountService) {
        this.operations = operations;
        this.accountService = accountService;
    }

    @PatchMapping("/deposit/account/{number}")
    public TransactionResponse depositFundsToAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody DepositRequest depositRequest) {
        Account destination = accountService.findAccountByNumber(accountNumber);

        DepositCommand depositCommand = DepositCommand.on(destination)
                .with(depositRequest.amount());

        OperationService<DepositCommand> deposit = operations.from(depositCommand);

        return TransactionResponse.from(deposit.process(depositCommand));
    }

    @PatchMapping("/withdrawal/account/{number}")
    public TransactionResponse withdrawFundsFromAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody WithdrawalRequest withdrawalRequest,
            @AuthenticationPrincipal Client client) {
        if (!accountService.accountBelongsToClient(accountNumber, client))
            throw new UnauthorizedAccessException();

        Account origin = accountService.findAccountByNumber(accountNumber);

        WithdrawalCommand withdrawalCommand = WithdrawalCommand.on(origin)
                .with(withdrawalRequest.amount());

        OperationService<WithdrawalCommand> withdrawal = operations.from(withdrawalCommand);

        return TransactionResponse.from(withdrawal.process(withdrawalCommand));
    }

    @PatchMapping("/transfer/account/{number}")
    public TransactionResponse transferFundsFromAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody TransferRequest transferRequest,
            @AuthenticationPrincipal Client client) {
        if (!accountService.accountBelongsToClient(accountNumber, client))
            throw new UnauthorizedAccessException();

        Account origin = accountService.findAccountByNumber(accountNumber);

        Account destination = accountService
                .findAccountByNumber(transferRequest.destinationNumber());

        TransferCommand transferCommand = TransferCommand.on(origin, destination)
                .with(transferRequest.amount());

        OperationService<TransferCommand> transfer = operations.from(transferCommand);

        return TransactionResponse.from(transfer.process(transferCommand));
    }
}

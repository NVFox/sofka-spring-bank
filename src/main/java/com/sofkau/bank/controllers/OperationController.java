package com.sofkau.bank.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sofkau.bank.commands.DepositCommand;
import com.sofkau.bank.commands.TransferCommand;
import com.sofkau.bank.commands.WithdrawalCommand;
import com.sofkau.bank.entities.Account;
import com.sofkau.bank.factories.OperationServiceFactory;
import com.sofkau.bank.http.requests.DepositRequest;
import com.sofkau.bank.http.requests.TransferRequest;
import com.sofkau.bank.http.requests.WithdrawalRequest;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void depositFundsToAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody DepositRequest depositRequest) {
        Account destination = accountService.findAccountByNumber(accountNumber);

        DepositCommand depositCommand = DepositCommand.on(destination)
                .with(depositRequest.amount());

        OperationService<DepositCommand> deposit = operations.from(depositCommand);

        deposit.process(depositCommand);
    }

    @PatchMapping("/withdrawal/account/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawFundsFromAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody WithdrawalRequest withdrawalRequest) {
        Account origin = accountService.findAccountByNumber(accountNumber);

        WithdrawalCommand withdrawalCommand = WithdrawalCommand.on(origin)
                .with(withdrawalRequest.amount());

        OperationService<WithdrawalCommand> withdrawal = operations.from(withdrawalCommand);

        withdrawal.process(withdrawalCommand);
    }

    @PatchMapping("/transfer/account/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferFundsFromAccount(
            @PathVariable("number") UUID accountNumber,
            @RequestBody TransferRequest transferRequest) {
        Account origin = accountService.findAccountByNumber(accountNumber);

        Account destination = accountService
                .findAccountByNumber(transferRequest.destinationNumber());

        TransferCommand transferCommand = TransferCommand.on(origin, destination)
                .with(transferRequest.amount());

        OperationService<TransferCommand> transfer = operations.from(transferCommand);

        transfer.process(transferCommand);
    }
}

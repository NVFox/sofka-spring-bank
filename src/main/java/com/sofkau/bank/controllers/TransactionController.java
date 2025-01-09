package com.sofkau.bank.controllers;

import java.util.List;
import java.util.UUID;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.exceptions.UnauthorizedAccessException;
import com.sofkau.bank.services.accounts.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sofkau.bank.http.responses.TransactionResponse;
import com.sofkau.bank.services.transactions.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping("/account/{number}")
    public List<TransactionResponse> findAccountTransactions(
            @PathVariable("number") UUID accountNumber,
            @AuthenticationPrincipal Client client) {
        if (!accountService.accountBelongsToClient(accountNumber, client))
            throw new UnauthorizedAccessException();

        return transactionService.findTransactionsByAccountNumber(accountNumber).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}

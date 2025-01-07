package com.sofkau.bank.controllers;

import java.util.List;
import java.util.UUID;

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

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/account/{number}")
    public List<TransactionResponse> findAccountTransactions(@PathVariable("number") UUID accountNumber) {
        return transactionService.findTransactionsByAccountNumber(accountNumber).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}

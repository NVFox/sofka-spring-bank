package com.sofkau.bank.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.User;
import com.sofkau.bank.entities.Account.Type;
import com.sofkau.bank.http.requests.CreateAccountRequest;
import com.sofkau.bank.http.responses.AccountResponse;
import com.sofkau.bank.services.accounts.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse createAccount(
            @RequestBody CreateAccountRequest accountRequest,
            @AuthenticationPrincipal Client client) {
        Type type = accountService
                .findAccountTypeByName(accountRequest.type());

        Account account = accountRequest.toAccount(type, client);

        return AccountResponse
                .from(accountService.createAccount(account));
    }

    @GetMapping
    public List<AccountResponse> findClientAccounts(@AuthenticationPrincipal Client client) {
        return accountService.findClientAccounts(client).stream()
                .map(AccountResponse::from)
                .toList();
    }
}

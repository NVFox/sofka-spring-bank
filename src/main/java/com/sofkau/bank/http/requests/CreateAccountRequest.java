package com.sofkau.bank.http.requests;

import java.math.BigDecimal;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Client;
import com.sofkau.bank.entities.Account.Type;

public record CreateAccountRequest(
        Type.Name type,
        BigDecimal openingBalance) {

    public Account toAccount(Type type, Client client) {
        return client.createAccount(type, openingBalance);
    }
}

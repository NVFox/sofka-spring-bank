package com.sofkau.bank.http.responses;

import java.math.BigDecimal;
import java.util.UUID;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Account.Type;

public record AccountResponse(
        Type.Name type,
        UUID number,
        BigDecimal balance) {

    public static AccountResponse from(Account account) {
        Type accountType = account.getType();
        Type.Name typeName = Type.Name.valueOf(accountType.getName());

        return new AccountResponse(typeName, account.getNumber(), account.getBalance());
    }

}

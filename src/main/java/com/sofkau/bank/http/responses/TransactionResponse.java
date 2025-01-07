package com.sofkau.bank.http.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;

public record TransactionResponse(
        int id,
        Action.Name action,
        BigDecimal previousBalance,
        BigDecimal currentBalance,
        LocalDateTime date) {

    public static TransactionResponse from(Transaction transaction) {
        Action action = transaction.getAction();
        Action.Name actionName = Action.Name.valueOf(action.getName());

        return new TransactionResponse(
                transaction.getId(),
                actionName,
                transaction.getPreviousBalance(),
                transaction.getCurrentBalance(),
                transaction.getDate());
    }
}

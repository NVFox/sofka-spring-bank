package com.sofkau.bank.http.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;
import com.sofkau.bank.entities.Transaction.Action.Name;

public record TransactionResponse(
        int id,
        Action.Name action,
        BigDecimal amount,
        String description,
        LocalDateTime date) {

    public static TransactionResponse from(Transaction transaction) {
        Action action = transaction.getAction();

        return new TransactionResponse(
                transaction.getId(),
                Name.valueOf(action.getName()),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate());
    }
}

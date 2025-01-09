package com.sofkau.bank.services.transactions;

import java.util.List;
import java.util.UUID;

import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    Action findTransactionActionByName(Action.Name name);

    List<Transaction> findTransactionsByAccountNumber(UUID number);
}

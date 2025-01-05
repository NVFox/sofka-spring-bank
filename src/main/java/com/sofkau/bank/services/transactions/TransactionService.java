package com.sofkau.bank.services.transactions;

import java.util.List;

import com.sofkau.bank.entities.Account;
import com.sofkau.bank.entities.Transaction;
import com.sofkau.bank.entities.Transaction.Action;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    Action findTransactionActionByName(Action.Name name);

    List<Transaction> findAccountTransactions(Account account);
}

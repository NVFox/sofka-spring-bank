package com.sofkau.bank.services.operations;

import com.sofkau.bank.entities.Transaction;

public interface OperationService<T> {
    Transaction process(T payload);
}

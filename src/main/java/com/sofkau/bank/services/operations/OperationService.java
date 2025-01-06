package com.sofkau.bank.services.operations;

public interface OperationService<T> {
    void process(T payload);
}

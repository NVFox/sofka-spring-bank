package com.sofkau.bank.exceptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException() {
        super("The resource already exists.");
    }
}

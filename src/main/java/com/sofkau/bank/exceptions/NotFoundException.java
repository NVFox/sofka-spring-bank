package com.sofkau.bank.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Resource requested wasn't found");
    }
}

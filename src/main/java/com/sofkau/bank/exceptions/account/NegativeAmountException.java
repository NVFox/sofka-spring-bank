package com.sofkau.bank.exceptions.account;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException() {
        super("The amount can't be negative.");
    }
}

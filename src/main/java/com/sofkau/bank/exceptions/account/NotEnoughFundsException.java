package com.sofkau.bank.exceptions.account;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException() {
        super("You don't have enough funds available.");
    }
}

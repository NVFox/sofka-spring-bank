package com.sofkau.bank.commands;

import java.math.BigDecimal;

import com.sofkau.bank.entities.Account;

public class TransferCommand {
    private final Account origin;
    private final Account destination;
    private final BigDecimal amount;

    private TransferCommand(
            Account origin,
            Account destination,
            BigDecimal amount) {
        this.origin = origin;
        this.destination = destination;
        this.amount = amount;
    }

    public static class Builder {
        private Account origin;
        private Account destination;

        private Builder(Account origin, Account destination) {
            this.origin = origin;
            this.destination = destination;
        }

        public TransferCommand with(BigDecimal amount) {
            return new TransferCommand(origin, destination, amount);
        }
    }

    public static Builder on(Account origin, Account destination) {
        return new Builder(origin, destination);
    }

    public Account getOrigin() {
        return this.origin;
    }

    public Account getDestination() {
        return this.destination;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}

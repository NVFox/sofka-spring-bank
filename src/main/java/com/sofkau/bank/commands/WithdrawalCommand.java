package com.sofkau.bank.commands;

import java.math.BigDecimal;

import com.sofkau.bank.entities.Account;

public non-sealed class WithdrawalCommand implements OperationalCommand {
    private final Account origin;
    private final BigDecimal amount;

    private WithdrawalCommand(Account origin, BigDecimal amount) {
        this.origin = origin;
        this.amount = amount;
    }

    public static class Builder {
        private Account account;

        private Builder(Account account) {
            this.account = account;
        }

        public WithdrawalCommand with(BigDecimal amount) {
            return new WithdrawalCommand(account, amount);
        }
    }

    public static Builder on(Account origin) {
        return new Builder(origin);
    }

    public Account getOrigin() {
        return this.origin;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}

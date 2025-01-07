package com.sofkau.bank.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.sofkau.bank.entities.Transaction.Action.Name;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "previous-balance")
    private BigDecimal previousBalance;

    @Column(name = "current-balance")
    private BigDecimal currentBalance;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {
    }

    @Entity
    @Table(name = "transaction_actions")
    public static class Action {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(unique = true)
        private String name;

        public enum Name {
            DEPOSIT, WITHDRAWAL, SENT_TRANSFER, RECEIVED_TRANSFER
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    public Transaction(Action action, Account account) {
        this.action = action;
        this.account = account;
    }

    public static class Builder {
        private final Account account;

        private Builder(Account account) {
            this.account = account;
        }

        public Transaction by(Action action, BigDecimal amount) {
            Action.Name actionName = Name.valueOf(action.name);
            Transaction transaction = new Transaction(action, account);

            transaction.previousBalance = account.getBalance();
            transaction.currentBalance = switch (actionName) {
                case DEPOSIT, RECEIVED_TRANSFER -> account.getBalance().add(amount);
                case WITHDRAWAL, SENT_TRANSFER -> account.getBalance().subtract(amount);
            };

            return transaction;
        }
    }

    public static Builder on(Account account) {
        return new Builder(account);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal balance) {
        this.previousBalance = balance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal balance) {
        this.currentBalance = balance;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

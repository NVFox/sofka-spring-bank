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

    @Column(name = "previous_balance")
    private BigDecimal previousBalance;

    private BigDecimal amount;

    @Column(name = "new_balance")
    private BigDecimal newBalance;

    private String description;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Account originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Account destinationAccount;

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

    public Transaction(Action action, Account origin, Account destination) {
        this.action = action;
        this.originAccount = origin;
        this.destinationAccount = destination;
    }

    public static class Builder {
        private final Account destination;
        private final Account origin;

        private Builder(Account origin, Account destination) {
            this.origin = origin;
            this.destination = destination;
        }

        public Transaction by(Action action, BigDecimal amount) {
            Action.Name actionName = Name.valueOf(action.name);
            Transaction transaction = new Transaction(action, origin, destination);

            Account subject = switch (actionName) {
                case DEPOSIT, RECEIVED_TRANSFER -> destination;
                case WITHDRAWAL, SENT_TRANSFER -> origin;
            };

            BigDecimal balance = subject.getBalance();

            transaction.description = descriptionBy(actionName, subject);
            transaction.amount = amount;
            transaction.previousBalance = balance;
            transaction.newBalance = subject == destination ? balance.add(amount) : balance.subtract(amount);

            return transaction;
        }

        private String descriptionBy(Action.Name actionName, Account subject) {
            return switch (actionName) {
                case DEPOSIT -> "Successful deposit to account " + subject.getNumber();
                case WITHDRAWAL -> "Successful withdrawal from account " + subject.getNumber();
                case SENT_TRANSFER -> "Successful transfer to account " + subject.getNumber();
                case RECEIVED_TRANSFER -> "Successful transfer from account " + subject.getNumber();
            };
        }
    }

    public static Builder from(Account origin) {
        return new Builder(origin, null);
    }

    public static Builder to(Account destination) {
        return new Builder(null, destination);
    }

    public static Builder on(Account origin, Account destination) {
        return new Builder(origin, destination);
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal balance) {
        this.newBalance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account account) {
        this.originAccount = account;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account account) {
        this.destinationAccount = account;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

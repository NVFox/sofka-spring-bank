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

    private BigDecimal amount;

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
            DEPOSIT, WITHDRAWAL, TRANSFER
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

            transaction.description = descriptionBy(actionName);
            transaction.amount = amount;

            return transaction;
        }

        private String descriptionBy(Action.Name actionName) {
            return switch (actionName) {
                case DEPOSIT -> "Successful deposit to account " + destination.getNumber();
                case WITHDRAWAL -> "Successful withdrawal from account " + origin.getNumber();
                case TRANSFER -> "Successful transfer from account " +
                        origin.getNumber() + " to account " + destination.getNumber();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

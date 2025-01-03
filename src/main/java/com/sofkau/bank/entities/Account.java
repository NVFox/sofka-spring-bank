package com.sofkau.bank.entities;

import java.math.BigDecimal;
import java.util.UUID;

import com.sofkau.bank.exceptions.account.NegativeAmountException;
import com.sofkau.bank.exceptions.account.NotEnoughFundsException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private UUID number;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Account() {
    }

    @Entity
    @Table(name = "account_types")
    public static class Type {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(unique = true)
        private String name;

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
    @JoinColumn(name = "type_id")
    private Type type;

    public Account(Type type, Client client, BigDecimal balance) {
        this.type = type;
        this.client = client;
        this.balance = balance;
    }

    @PrePersist
    public void init() {
        number = number == null
                ? UUID.randomUUID()
                : number;
    }

    public void depositFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException();

        balance = balance.add(amount);
    }

    public void withdrawFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException();

        if (balance.compareTo(amount) < 0)
            throw new NotEnoughFundsException();

        balance = balance.subtract(amount);
    }

    public void transferFunds(BigDecimal amount, Account to) {
        this.withdrawFunds(amount);
        to.depositFunds(amount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getNumber() {
        return number;
    }

    public void setNumber(UUID number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

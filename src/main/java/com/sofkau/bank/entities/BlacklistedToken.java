package com.sofkau.bank.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String token;

    public BlacklistedToken(String token) {
        this.token = token;
    }
}

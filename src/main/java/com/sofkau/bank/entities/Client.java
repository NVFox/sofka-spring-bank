package com.sofkau.bank.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Client() {
    }

    public Client(String firstName, String lastName, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = user;
    }

    public static class Builder {
        private User user;
        private String firstName;
        private String lastName;

        private Builder(User user) {
            this.user = user;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName.trim();
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName.trim();
            return this;
        }

        public Client build() {
            return new Client(firstName, lastName, user);
        }
    }

    public static Builder from(User user) {
        return new Builder(user);
    }

    public Account createAccount(Account.Type type) {
        return createAccount(type, BigDecimal.ZERO);
    }

    public Account createAccount(Account.Type type, BigDecimal amount) {
        return new Account(type, this, amount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

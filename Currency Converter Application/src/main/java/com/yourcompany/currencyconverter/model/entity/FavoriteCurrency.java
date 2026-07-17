package com.yourcompany.currencyconverter.model.entity;

import jakarta.persistence.*;

/**
 * Entity representing a favorite currency marked by the user.
 * Assumes a single-user application (no user_id column).
 */
@Entity
@Table(name = "favorite_currency")
public class FavoriteCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_code", nullable = false, unique = true)
    private Currency currency;

    public FavoriteCurrency() {}

    public FavoriteCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
}

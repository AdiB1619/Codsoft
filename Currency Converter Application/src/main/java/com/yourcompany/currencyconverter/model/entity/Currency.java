package com.yourcompany.currencyconverter.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a supported ISO 4217 currency.
 *
 * <p>Maps to the {@code currencies} table in MySQL. The primary key is the
 * three-letter ISO currency code (e.g. "USD"), which is both unique and human-readable,
 * so a surrogate auto-increment key is unnecessary here.
 *
 * <p>Design notes:
 * <ul>
 *   <li>No Lombok is used intentionally — explicit constructors and accessors keep the
 *       class transparent and easy to understand for all readers.</li>
 *   <li>{@code name} and {@code symbol} columns have a max length matching common
 *       international currency names (e.g. "Salvadoran Colón" = 19 chars).</li>
 *   <li>All fields are {@code nullable = false} to enforce data integrity at the DB level
 *       in addition to application-level validation.</li>
 * </ul>
 */
@Entity
@Table(name = "currencies")
public class Currency {

    /** ISO 4217 three-letter currency code (e.g. "USD", "EUR"). Acts as the primary key. */
    @Id
    @Column(name = "code", length = 3, nullable = false, updatable = false)
    private String code;

    /** Full English name of the currency (e.g. "US Dollar", "Euro"). */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /** Currency symbol used for display (e.g. "$", "€", "¥"). */
    @Column(name = "symbol", length = 10, nullable = false)
    private String symbol;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Required no-arg constructor for JPA/Hibernate. */
    protected Currency() {
    }

    /**
     * Full constructor for creating a new Currency record.
     *
     * @param code   ISO 4217 three-letter code (uppercase, e.g. "USD")
     * @param name   full currency name (e.g. "US Dollar")
     * @param symbol display symbol (e.g. "$")
     */
    public Currency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Currency{code='" + code + "', name='" + name + "', symbol='" + symbol + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency other)) return false;
        return code != null && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

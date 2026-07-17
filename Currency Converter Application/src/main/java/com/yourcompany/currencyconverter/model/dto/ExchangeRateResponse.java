package com.yourcompany.currencyconverter.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unified response model returned by any {@code ExchangeRateProvider} implementation.
 *
 * <p>This DTO decouples the internal application from the specific JSON schema
 * of each external API. Regardless of whether the data comes from ExchangeRate-API,
 * Frankfurter, or any other provider, the rest of the application always receives
 * this standard structure.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code from}     – source currency ISO 4217 code (e.g. "USD").</li>
 *   <li>{@code to}       – target currency ISO 4217 code (e.g. "EUR").</li>
 *   <li>{@code rate}     – how many units of {@code to} equal one unit of {@code from}
 *                          (stored as {@link BigDecimal} for financial precision).</li>
 *   <li>{@code rateDate} – the date on which the provider last updated this rate.</li>
 * </ul>
 */
public class ExchangeRateResponse {

    private final String from;
    private final String to;
    private final BigDecimal rate;
    private final LocalDate rateDate;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates an immutable exchange-rate response.
     *
     * @param from     source currency code
     * @param to       target currency code
     * @param rate     exchange rate (target/source), must be positive
     * @param rateDate date the provider last updated the rate
     */
    public ExchangeRateResponse(String from, String to, BigDecimal rate, LocalDate rateDate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.rateDate = rateDate;
    }

    // -------------------------------------------------------------------------
    // Accessors (immutable — no setters)
    // -------------------------------------------------------------------------

    /** @return the source ISO 4217 currency code */
    public String getFrom() {
        return from;
    }

    /** @return the target ISO 4217 currency code */
    public String getTo() {
        return to;
    }

    /**
     * @return the exchange rate as a {@link BigDecimal}.
     *         Using BigDecimal avoids floating-point rounding errors in financial calculations.
     */
    public BigDecimal getRate() {
        return rate;
    }

    /** @return the date this rate was last published by the provider */
    public LocalDate getRateDate() {
        return rateDate;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ExchangeRateResponse{from='" + from + "', to='" + to
                + "', rate=" + rate + ", rateDate=" + rateDate + '}';
    }
}

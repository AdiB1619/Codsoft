package com.yourcompany.currencyconverter.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response body model returned by the {@code POST /api/convert} endpoint.
 *
 * <p>This is an <strong>immutable</strong> DTO — all fields are set via the
 * constructor and exposed through read-only getters. Immutability makes this
 * object safe to pass across threads, cache, or log without defensive copying.
 *
 * <p>It has <strong>no JPA annotations</strong> and no persistence concern;
 * it is a pure data carrier that the controller serialises to JSON for the client.
 *
 * <h2>Field descriptions</h2>
 * <ul>
 *   <li>{@code from}      – source ISO 4217 currency code (e.g. "USD").</li>
 *   <li>{@code to}        – target ISO 4217 currency code (e.g. "EUR").</li>
 *   <li>{@code amount}    – original amount the user requested to convert.</li>
 *   <li>{@code result}    – converted amount ({@code amount × rate}), rounded to 4 d.p.</li>
 *   <li>{@code rate}      – exchange rate used (target units per 1 source unit).</li>
 *   <li>{@code timestamp} – server time when the conversion was performed (UTC).</li>
 * </ul>
 *
 * <h2>Sample JSON output</h2>
 * <pre>{@code
 * {
 *   "from":      "USD",
 *   "to":        "EUR",
 *   "amount":    100.00,
 *   "result":    85.4200,
 *   "rate":      0.854200,
 *   "timestamp": "2026-07-17T18:34:10"
 * }
 * }</pre>
 */
public class ConversionResponse {

    /** Source ISO 4217 currency code (e.g. "USD"). */
    private final String from;

    /** Target ISO 4217 currency code (e.g. "EUR"). */
    private final String to;

    /** Original amount submitted in the conversion request. */
    private final BigDecimal amount;

    /**
     * Converted amount after applying the exchange rate.
     * Calculated as: {@code amount.multiply(rate)}.
     * Stored with 4 decimal places for precision.
     */
    private final BigDecimal result;

    /**
     * Exchange rate used for this conversion (target units per 1 source unit).
     * Stored with 6 decimal places to match the precision of most providers.
     */
    private final BigDecimal rate;

    /**
     * Server-side timestamp of when the conversion was performed (ISO-8601 format).
     * Serialised as "yyyy-MM-dd'T'HH:mm:ss" (no timezone — server is UTC).
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs an immutable conversion response.
     *
     * @param from      source currency code
     * @param to        target currency code
     * @param amount    original amount
     * @param result    converted amount (amount × rate)
     * @param rate      exchange rate used
     * @param timestamp time the conversion was performed
     */
    public ConversionResponse(String from,
                               String to,
                               BigDecimal amount,
                               BigDecimal result,
                               BigDecimal rate,
                               LocalDateTime timestamp) {
        this.from      = from;
        this.to        = to;
        this.amount    = amount;
        this.result    = result;
        this.rate      = rate;
        this.timestamp = timestamp;
    }

    // -------------------------------------------------------------------------
    // Getters (no setters — immutable)
    // -------------------------------------------------------------------------

    /** @return source ISO 4217 currency code */
    public String getFrom() {
        return from;
    }

    /** @return target ISO 4217 currency code */
    public String getTo() {
        return to;
    }

    /** @return original amount submitted by the client */
    public BigDecimal getAmount() {
        return amount;
    }

    /** @return converted amount (amount × rate) */
    public BigDecimal getResult() {
        return result;
    }

    /** @return exchange rate used for the conversion */
    public BigDecimal getRate() {
        return rate;
    }

    /** @return ISO-8601 timestamp of when the conversion was performed */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ConversionResponse{"
                + "from='" + from + '\''
                + ", to='" + to + '\''
                + ", amount=" + amount
                + ", result=" + result
                + ", rate=" + rate
                + ", timestamp=" + timestamp
                + '}';
    }
}

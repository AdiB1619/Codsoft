package com.yourcompany.currencyconverter.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * Request body model for the {@code POST /api/convert} endpoint.
 *
 * <p>This is a plain DTO — it has <strong>no JPA annotations</strong> and no
 * persistence concern. Its sole purpose is to carry and validate the input data
 * arriving from the client before it reaches the service layer.
 *
 * <h2>Validation rules (enforced by {@code @Valid} in the controller)</h2>
 * <ul>
 *   <li>{@code from} – required, exactly 3 uppercase ISO 4217 letters (e.g. "USD").</li>
 *   <li>{@code to}   – required, exactly 3 uppercase ISO 4217 letters (e.g. "EUR").</li>
 *   <li>{@code amount} – required, must be ≥ 0.01 (positive, non-zero).</li>
 * </ul>
 *
 * <p>If any rule is violated, Spring's {@code MethodArgumentNotValidException} is thrown,
 * which the {@code GlobalExceptionHandler} maps to HTTP 400 with field-level error details.
 *
 * <p>Regex explanation for currency codes: {@code ^[A-Z]{3}$}
 * <ul>
 *   <li>{@code ^} – start of string</li>
 *   <li>{@code [A-Z]{3}} – exactly 3 uppercase ASCII letters</li>
 *   <li>{@code $} – end of string</li>
 * </ul>
 */
public class ConversionRequest {

    /**
     * Source currency ISO 4217 code.
     * Must be exactly 3 uppercase letters (e.g. "USD", "GBP").
     */
    @NotBlank(message = "Source currency code must not be blank")
    @Pattern(
            regexp  = "^[A-Z]{3}$",
            message = "Source currency must be a valid 3-letter ISO 4217 code (e.g. USD)"
    )
    private String from;

    /**
     * Target currency ISO 4217 code.
     * Must be exactly 3 uppercase letters (e.g. "EUR", "JPY").
     */
    @NotBlank(message = "Target currency code must not be blank")
    @Pattern(
            regexp  = "^[A-Z]{3}$",
            message = "Target currency must be a valid 3-letter ISO 4217 code (e.g. EUR)"
    )
    private String to;

    /**
     * Amount to convert. Must be a positive decimal ≥ 0.01.
     *
     * <p>Using {@link BigDecimal} avoids floating-point precision issues that
     * {@code float} or {@code double} would introduce in financial calculations.
     */
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero (minimum 0.01)")
    private BigDecimal amount;

    // -------------------------------------------------------------------------
    // No-arg constructor (required for Jackson deserialization)
    // -------------------------------------------------------------------------

    /** Default constructor for Jackson JSON deserialization. */
    public ConversionRequest() {
    }

    /**
     * Full constructor — useful in tests and service code.
     *
     * @param from   source currency ISO code
     * @param to     target currency ISO code
     * @param amount amount to convert (must be ≥ 0.01)
     */
    public ConversionRequest(String from, String to, BigDecimal amount) {
        this.from   = from;
        this.to     = to;
        this.amount = amount;
    }

    // -------------------------------------------------------------------------
    // Getters and setters (required for Jackson serialization/deserialization)
    // -------------------------------------------------------------------------

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ConversionRequest{from='" + from + "', to='" + to + "', amount=" + amount + '}';
    }
}

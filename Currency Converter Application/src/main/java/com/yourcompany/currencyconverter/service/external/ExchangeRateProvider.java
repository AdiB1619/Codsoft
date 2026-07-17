package com.yourcompany.currencyconverter.service.external;

import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;

import java.math.BigDecimal;

/**
 * Abstraction layer for any external exchange-rate data provider.
 *
 * <p>Implementing this interface decouples the rest of the application from any
 * specific third-party API. Switching between providers (e.g. ExchangeRate-API,
 * Frankfurter, CurrencyFreaks) requires only a new implementation of this interface
 * — no changes to the service or controller layers.
 *
 * <p>Current implementations:
 * <ul>
 *   <li>{@link ExchangeRateApiClient} – ExchangeRate-API v6 (primary, requires API key).</li>
 * </ul>
 *
 * <p>Usage pattern:
 * <pre>{@code
 * @Autowired
 * private ExchangeRateProvider provider;
 *
 * BigDecimal rate = provider.getExchangeRate("USD", "EUR");
 * }</pre>
 */
public interface ExchangeRateProvider {

    /**
     * Returns the current exchange rate from {@code from} to {@code to}.
     *
     * <p>The returned value represents how many units of {@code to} one unit
     * of {@code from} is worth. For example, if USD→EUR = 0.85, then
     * 100 USD converts to 85 EUR.
     *
     * @param from the source ISO 4217 currency code (e.g. "USD")
     * @param to   the target ISO 4217 currency code (e.g. "EUR")
     * @return exchange rate as a {@link BigDecimal} (never {@code null})
     * @throws com.yourcompany.currencyconverter.exception.ExternalApiException
     *         if the external API is unreachable or returns an error
     */
    BigDecimal getExchangeRate(String from, String to);

    /**
     * Returns full exchange-rate details (rate + metadata) for the given pair.
     *
     * <p>Used by {@code ConversionService} to persist the rate timestamp alongside
     * each conversion history record.
     *
     * @param from the source ISO 4217 currency code
     * @param to   the target ISO 4217 currency code
     * @return a {@link ExchangeRateResponse} containing rate, date, and codes
     * @throws com.yourcompany.currencyconverter.exception.ExternalApiException
     *         if the external API is unreachable or returns an error
     */
    ExchangeRateResponse getRateDetails(String from, String to);
}

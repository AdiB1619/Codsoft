package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import com.yourcompany.currencyconverter.repository.CurrencyRepository;
import com.yourcompany.currencyconverter.service.external.ExchangeRateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Core business service for performing currency conversions.
 *
 * <h2>Responsibilities</h2>
 * <ol>
 *   <li><strong>Validate</strong> – verify both currency codes exist in the local
 *       {@code currencies} database table via {@link CurrencyRepository}.</li>
 *   <li><strong>Fetch rate</strong> – retrieve the live exchange rate from the
 *       external provider via the {@link ExchangeRateProvider} abstraction.</li>
 *   <li><strong>Calculate</strong> – multiply {@code amount × rate}, rounded to
 *       4 decimal places using {@link RoundingMode#HALF_UP}.</li>
 *   <li><strong>Return</strong> – package the result into an immutable
 *       {@link ConversionResponse} DTO with a server-side timestamp.</li>
 * </ol>
 *
 * <h2>Math precision</h2>
 * <p>All arithmetic is performed using {@link BigDecimal} with
 * {@link RoundingMode#HALF_UP} — the standard rounding mode for currency
 * to avoid systematic under/over-estimation.
 *
 * <h2>History persistence</h2>
 * <p>Saving the conversion to {@code ConversionHistory} will be added in
 * <strong>Milestone 9</strong>. The relevant hook point is marked with a TODO
 * comment inside {@link #convert(String, String, BigDecimal)}.
 *
 * <h2>Dependency injection</h2>
 * <p>Depends on the {@link ExchangeRateProvider} <em>interface</em>, not any
 * concrete implementation. This allows swapping providers (e.g. Frankfurter,
 * CurrencyFreaks) without touching this class.
 *
 * @see ExchangeRateProvider
 * @see CurrencyRepository
 */
@Service
public class ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);

    /**
     * Number of decimal places to use when rounding the converted result.
     * 4 d.p. matches international banking precision standards (e.g. ISO 20022).
     */
    private static final int RESULT_SCALE = 4;

    private final ExchangeRateProvider exchangeRateProvider;
    private final CurrencyRepository currencyRepository;

    // -------------------------------------------------------------------------
    // Constructor injection
    // -------------------------------------------------------------------------

    /**
     * Constructs the service with its required dependencies.
     *
     * @param exchangeRateProvider provider for live exchange rates (injected by Spring)
     * @param currencyRepository   JPA repository for validating supported currencies
     */
    public ConversionService(ExchangeRateProvider exchangeRateProvider,
                             CurrencyRepository currencyRepository) {
        this.exchangeRateProvider = exchangeRateProvider;
        this.currencyRepository   = currencyRepository;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Converts {@code amount} of {@code from} currency into {@code to} currency.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Validate that {@code from} is a supported currency in the database.</li>
     *   <li>Validate that {@code to} is a supported currency in the database.</li>
     *   <li>Fetch the live exchange rate from the external provider.</li>
     *   <li>Calculate {@code result = amount × rate} (scaled to {@value #RESULT_SCALE} d.p.,
     *       {@link RoundingMode#HALF_UP}).</li>
     *   <li>Stamp the server-side timestamp and return the {@link ConversionResponse}.</li>
     * </ol>
     *
     * @param from   source ISO 4217 currency code (e.g. "USD")
     * @param to     target ISO 4217 currency code (e.g. "EUR")
     * @param amount amount to convert — must be positive and non-zero
     * @return a fully-populated {@link ConversionResponse} (never {@code null})
     * @throws ResourceNotFoundException if {@code from} or {@code to} is not in the currencies table
     * @throws com.yourcompany.currencyconverter.exception.ExternalApiException
     *         if the external rate provider is unavailable or returns an error
     */
    public ConversionResponse convert(String from, String to, BigDecimal amount) {
        log.info("Converting {} {} → {}", amount, from, to);

        // Step 1 & 2: Validate both currency codes against the local database
        validateSupportedCurrency(from);
        validateSupportedCurrency(to);

        // Step 3: Fetch live exchange rate details (includes rate + date)
        ExchangeRateResponse rateDetails = exchangeRateProvider.getRateDetails(from, to);
        BigDecimal rate = rateDetails.getRate();
        log.debug("Rate fetched: 1 {} = {} {}", from, rate, to);

        // Step 4: Perform the conversion with 4 d.p. HALF_UP rounding
        BigDecimal result = amount
                .multiply(rate)
                .setScale(RESULT_SCALE, RoundingMode.HALF_UP);

        log.info("Conversion result: {} {} = {} {}", amount, from, result, to);

        // TODO (Milestone 9): Persist conversion to ConversionHistory here

        // Step 5: Return the immutable response DTO
        return new ConversionResponse(from, to, amount, result, rate, LocalDateTime.now());
    }

    /**
     * Overload that accepts a {@link ConversionRequest} DTO directly.
     *
     * <p>Delegates to {@link #convert(String, String, BigDecimal)} — the controller
     * can call this overload to avoid unpacking the DTO fields manually.
     *
     * @param request the validated conversion request DTO
     * @return a fully-populated {@link ConversionResponse}
     */
    public ConversionResponse convert(ConversionRequest request) {
        return convert(request.getFrom(), request.getTo(), request.getAmount());
    }

    /**
     * Returns all currencies supported by the application.
     *
     * <p>Used by {@code GET /api/currencies} to let clients discover which
     * currency codes are valid inputs for the {@code /api/convert} endpoint.
     *
     * @return an unmodifiable list of all {@link com.yourcompany.currencyconverter.model.entity.Currency}
     *         records stored in the database
     */
    public java.util.List<com.yourcompany.currencyconverter.model.entity.Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Validates that the given currency code is supported by looking it up in the
     * local {@code currencies} database table.
     *
     * @param code ISO 4217 currency code to validate
     * @throws ResourceNotFoundException if the code is not present in the database
     */
    private void validateSupportedCurrency(String code) {
        if (!currencyRepository.existsById(code)) {
            throw new ResourceNotFoundException(
                    "Currency not supported: '" + code + "'. "
                    + "Please use a currency from the /api/currencies endpoint.");
        }
    }
}

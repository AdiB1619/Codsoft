package com.yourcompany.currencyconverter.service.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yourcompany.currencyconverter.exception.ExternalApiException;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fetches live exchange rates from the <a href="https://www.exchangerate-api.com/">ExchangeRate-API v6</a>
 * using its Pair Conversion endpoint.
 *
 * <h2>Endpoint used</h2>
 * <pre>
 *   GET https://v6.exchangerate-api.com/v6/{API_KEY}/pair/{base}/{target}
 * </pre>
 *
 * <h2>Sample response</h2>
 * <pre>{@code
 * {
 *   "result": "success",
 *   "base_code": "USD",
 *   "target_code": "EUR",
 *   "conversion_rate": 0.8542,
 *   "time_last_update_utc": "Thu, 17 Jul 2026 00:00:02 +0000"
 * }
 * }</pre>
 *
 * <h2>Retry strategy</h2>
 * On a transient {@link RestClientException}, the client retries once before
 * throwing an {@link ExternalApiException}. This gives resilience against
 * brief network blips without adding complex retry infrastructure.
 *
 * <h2>Configuration</h2>
 * <ul>
 *   <li>{@code exchangerate.api.url}  – base URL (default: ExchangeRate-API v6).</li>
 *   <li>{@code exchangerate.api.key}  – API key; set via environment variable
 *       {@code EXCHANGERATE_API_KEY} in production.</li>
 * </ul>
 *
 * @see ExchangeRateProvider
 */
@Component
public class ExchangeRateApiClient implements ExchangeRateProvider {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateApiClient.class);

    /** Maximum number of attempts before giving up (1 initial + 1 retry). */
    private static final int MAX_ATTEMPTS = 2;

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final String apiKey;

    // -------------------------------------------------------------------------
    // Constructor injection (preferred over @Autowired on fields)
    // -------------------------------------------------------------------------

    public ExchangeRateApiClient(
            RestTemplate restTemplate,
            @Value("${exchangerate.api.url}") String apiBaseUrl,
            @Value("${exchangerate.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
        this.apiKey = apiKey;
    }

    // -------------------------------------------------------------------------
    // ExchangeRateProvider implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Calls the ExchangeRate-API pair conversion endpoint and extracts the
     * {@code conversion_rate} field. Retries once on transient failures.
     */
    @Override
    public BigDecimal getExchangeRate(String from, String to) {
        return getRateDetails(from, to).getRate();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Calls the ExchangeRate-API pair conversion endpoint. On a failed attempt,
     * the client waits briefly and retries once. If the second attempt also fails,
     * an {@link ExternalApiException} is propagated to the caller.
     */
    @Override
    public ExchangeRateResponse getRateDetails(String from, String to) {
        if ("YOUR_API_KEY_HERE".equals(apiKey)) {
            log.warn("API key is not configured. Falling back to open.er-api.com for {} -> {}", from, to);
            try {
                String openUrl = "https://open.er-api.com/v6/latest/" + from;
                OpenApiResponse openResponse = restTemplate.getForObject(openUrl, OpenApiResponse.class);
                if (openResponse != null && "success".equals(openResponse.result) && openResponse.rates != null) {
                    BigDecimal rate = openResponse.rates.get(to);
                    if (rate != null) {
                        return new ExchangeRateResponse(from, to, rate, LocalDate.now());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to fetch from open API fallback", e);
            }
            throw new ExternalApiException("API key is not configured and open API fallback failed.");
        }

        String url = buildUrl(from, to);
        log.info("Fetching exchange rate: {} → {} from {}", from, to, apiBaseUrl);

        RestClientException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ApiPairResponse apiResponse = restTemplate.getForObject(url, ApiPairResponse.class);
                return mapToExchangeRateResponse(apiResponse, from, to);

            } catch (RestClientException ex) {
                lastException = ex;
                log.warn("Exchange rate API call failed (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, ex.getMessage());

                if (attempt < MAX_ATTEMPTS) {
                    pauseBeforeRetry();
                }
            }
        }

        // All attempts exhausted — wrap and re-throw
        throw new ExternalApiException(
                "Exchange rate service unavailable after " + MAX_ATTEMPTS + " attempts for pair "
                        + from + "/" + to,
                lastException);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds the pair-conversion URL for the given currency pair.
     *
     * @param from source currency code
     * @param to   target currency code
     * @return fully-formed URL string
     */
    private String buildUrl(String from, String to) {
        return apiBaseUrl + "/" + apiKey + "/pair/" + from + "/" + to;
    }

    /**
     * Maps the raw API JSON response to the internal {@link ExchangeRateResponse} DTO.
     *
     * @param apiResponse the deserialized API response object
     * @param from        expected source currency code (used as fallback if API field is null)
     * @param to          expected target currency code (used as fallback if API field is null)
     * @return a populated {@link ExchangeRateResponse}
     * @throws ExternalApiException if the API signals an error in its {@code result} field
     *                              or if the conversion rate is missing
     */
    private ExchangeRateResponse mapToExchangeRateResponse(ApiPairResponse apiResponse,
                                                            String from, String to) {
        if (apiResponse == null) {
            throw new ExternalApiException(
                    "Exchange rate API returned an empty response for " + from + "/" + to);
        }

        if (!"success".equalsIgnoreCase(apiResponse.result)) {
            throw new ExternalApiException(
                    "Exchange rate API returned error for " + from + "/" + to
                            + ": " + apiResponse.errorType);
        }

        if (apiResponse.conversionRate == null) {
            throw new ExternalApiException(
                    "Exchange rate API response missing 'conversion_rate' field for " + from + "/" + to);
        }

        log.debug("Received rate {} → {}: {}", from, to, apiResponse.conversionRate);

        return new ExchangeRateResponse(
                apiResponse.baseCode != null ? apiResponse.baseCode : from,
                apiResponse.targetCode != null ? apiResponse.targetCode : to,
                apiResponse.conversionRate,
                LocalDate.now()   // ExchangeRate-API free tier: daily updates; use today's date
        );
    }

    /**
     * Brief pause before a retry attempt.
     * Swallows {@link InterruptedException} and restores the interrupted flag.
     */
    private void pauseBeforeRetry() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    // -------------------------------------------------------------------------
    // Internal DTO — maps the raw JSON from ExchangeRate-API
    // -------------------------------------------------------------------------

    /**
     * Internal Jackson-mapped DTO for the ExchangeRate-API pair-conversion response.
     *
     * <p>Intentionally package-private and kept inside the client class to prevent
     * external API contract leaking into the rest of the application — only the
     * canonical {@link ExchangeRateResponse} DTO is exposed externally.
     *
     * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} ensures the client
     * is resilient to new fields the API may add in future without breaking
     * deserialization.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ApiPairResponse {

        /** "success" or "error" */
        @JsonProperty("result")
        String result;

        /** Error type string returned when result is "error" (e.g. "invalid-key") */
        @JsonProperty("error-type")
        String errorType;

        /** Source currency code, e.g. "USD" */
        @JsonProperty("base_code")
        String baseCode;

        /** Target currency code, e.g. "EUR" */
        @JsonProperty("target_code")
        String targetCode;

        /** The exchange rate (target units per 1 source unit) */
        @JsonProperty("conversion_rate")
        BigDecimal conversionRate;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenApiResponse {
        @JsonProperty("result")
        String result;

        @JsonProperty("base_code")
        String baseCode;

        @JsonProperty("rates")
        java.util.Map<String, BigDecimal> rates;
    }
}

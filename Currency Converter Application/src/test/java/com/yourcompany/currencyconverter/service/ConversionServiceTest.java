package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.ExternalApiException;
import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import com.yourcompany.currencyconverter.repository.CurrencyRepository;
import com.yourcompany.currencyconverter.service.external.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ConversionService}.
 *
 * <p>Both {@link ExchangeRateProvider} and {@link CurrencyRepository} are mocked
 * with Mockito — no Spring context, no database, no HTTP calls. Tests are fast
 * and fully isolated.
 *
 * <p>Covers:
 * <ul>
 *   <li>Primary acceptance criterion: convert("USD","EUR",100) = 85 (rate 0.85).</li>
 *   <li>BigDecimal precision and HALF_UP rounding to 4 decimal places.</li>
 *   <li>{@link ConversionResponse} field population (from/to/amount/result/rate/timestamp).</li>
 *   <li>Unsupported {@code from} currency → {@link ResourceNotFoundException}.</li>
 *   <li>Unsupported {@code to} currency → {@link ResourceNotFoundException}.</li>
 *   <li>External API failure propagates as {@link ExternalApiException}.</li>
 *   <li>Same-currency conversion (USD→USD, rate=1.0).</li>
 *   <li>Very small amounts (fractional cents) rounded correctly.</li>
 *   <li>Large amounts retain full precision.</li>
 *   <li>ConversionRequest DTO overload delegates correctly.</li>
 *   <li>Timestamp is populated and recent (within 5 seconds of now).</li>
 *   <li>ExchangeRateProvider is called exactly once per convert().</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConversionService – Unit Tests")
class ConversionServiceTest {

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private ConversionService conversionService;

    @BeforeEach
    void setUpCurrencyValidation() {
        // Default: all currency codes exist in the database.
        // Individual tests override this for error-case scenarios.
        lenient().when(currencyRepository.existsById(anyString())).thenReturn(true);
    }

    // -------------------------------------------------------------------------
    // Primary acceptance-criteria test
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("convert(USD, EUR, 100) with rate 0.85 returns result 85.0000")
    void convert_usdToEur_100units_rate085_returns85() {
        // Arrange
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        // Act
        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("100"));

        // Assert — primary acceptance criterion
        assertThat(response.getResult())
                .isEqualByComparingTo(new BigDecimal("85.0000"));
    }

    // -------------------------------------------------------------------------
    // Full response DTO field validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("convert() populates all ConversionResponse fields correctly")
    void convert_populatesAllResponseFields() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        // Act
        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("250.00"));

        // Assert — every field
        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getTo()).isEqualTo("EUR");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("212.5000"));
        assertThat(response.getRate()).isEqualByComparingTo(new BigDecimal("0.85"));
        assertThat(response.getTimestamp()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("Timestamp is populated and within 5 seconds of the call")
    void convert_timestamp_isRecentAndNotNull() {
        // Arrange
        mockRate("GBP", "JPY", new BigDecimal("198.53"));
        LocalDateTime before = LocalDateTime.now();

        // Act
        ConversionResponse response = conversionService.convert("GBP", "JPY", new BigDecimal("10"));

        // Assert
        assertThat(response.getTimestamp())
                .isNotNull()
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(before.plusSeconds(5));
    }

    // -------------------------------------------------------------------------
    // Rounding precision tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Result is rounded to exactly 4 decimal places (HALF_UP)")
    void convert_result_isRoundedToFourDecimalPlaces() {
        // 100 × 0.8542 = 85.42 → scale=4: 85.4200
        mockRate("USD", "EUR", new BigDecimal("0.8542"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("100"));

        assertThat(response.getResult().scale()).isEqualTo(4);
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("85.4200"));
    }

    @Test
    @DisplayName("HALF_UP rounding: 1 × 0.33335 rounds up to 0.3334")
    void convert_halfUpRounding_roundsCorrectly() {
        // 1 × 0.33335 = 0.33335 → HALF_UP at 4 d.p. = 0.3334
        mockRate("USD", "EUR", new BigDecimal("0.33335"));

        ConversionResponse response = conversionService.convert("USD", "EUR", BigDecimal.ONE);

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("0.3334"));
    }

    @Test
    @DisplayName("Same-currency conversion (USD→USD, rate=1.0) returns original amount")
    void convert_sameCurrency_returnsOriginalAmount() {
        mockRate("USD", "USD", BigDecimal.ONE);

        ConversionResponse response = conversionService.convert("USD", "USD", new BigDecimal("500.00"));

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Very small amount (0.01) converted correctly")
    void convert_minimumAmount_convertedCorrectly() {
        // 0.01 × 0.85 = 0.0085 → scale=4: 0.0085
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("0.01"));

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("0.0085"));
    }

    @Test
    @DisplayName("Large amount (1,000,000) retains full precision")
    void convert_largeAmount_retainsFullPrecision() {
        // 1_000_000 × 0.8542 = 854_200.0000
        mockRate("USD", "EUR", new BigDecimal("0.8542"));

        ConversionResponse response = conversionService.convert(
                "USD", "EUR", new BigDecimal("1000000"));

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("854200.0000"));
    }

    // -------------------------------------------------------------------------
    // Currency code validation tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Unsupported 'from' currency throws ResourceNotFoundException")
    void convert_unsupportedFromCurrency_throwsResourceNotFoundException() {
        // Override default mock: "XYZ" does not exist
        when(currencyRepository.existsById("XYZ")).thenReturn(false);

        assertThatThrownBy(() -> conversionService.convert("XYZ", "EUR", new BigDecimal("100")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("XYZ");

        // Provider should NOT be called — validation must short-circuit
        verifyNoInteractions(exchangeRateProvider);
    }

    @Test
    @DisplayName("Unsupported 'to' currency throws ResourceNotFoundException")
    void convert_unsupportedToCurrency_throwsResourceNotFoundException() {
        when(currencyRepository.existsById("USD")).thenReturn(true);
        when(currencyRepository.existsById("ZZZ")).thenReturn(false);

        assertThatThrownBy(() -> conversionService.convert("USD", "ZZZ", new BigDecimal("100")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ZZZ");

        verifyNoInteractions(exchangeRateProvider);
    }

    @Test
    @DisplayName("'from' is validated first — 'to' is not checked if 'from' is invalid")
    void convert_fromValidatedBeforeTo() {
        when(currencyRepository.existsById("BAD")).thenReturn(false);
        // "EUR" would return true from the lenient default, but should never be reached

        assertThatThrownBy(() -> conversionService.convert("BAD", "EUR", BigDecimal.TEN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BAD");

        // existsById("EUR") should never have been called
        verify(currencyRepository, never()).existsById("EUR");
    }

    // -------------------------------------------------------------------------
    // External API failure propagation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ExternalApiException from provider propagates to caller unchanged")
    void convert_externalApiFailure_propagatesExternalApiException() {
        when(exchangeRateProvider.getRateDetails("USD", "EUR"))
                .thenThrow(new ExternalApiException("API timeout"));

        assertThatThrownBy(() -> conversionService.convert("USD", "EUR", new BigDecimal("100")))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("API timeout");
    }

    // -------------------------------------------------------------------------
    // ConversionRequest DTO overload
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("convert(ConversionRequest) overload delegates to convert(String, String, BigDecimal)")
    void convert_requestOverload_delegatesCorrectly() {
        mockRate("USD", "EUR", new BigDecimal("0.85"));
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("100"));

        ConversionResponse response = conversionService.convert(request);

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("85.0000"));
        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getTo()).isEqualTo("EUR");
    }

    // -------------------------------------------------------------------------
    // Interaction verification
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ExchangeRateProvider.getRateDetails() is called exactly once per convert()")
    void convert_callsRateProviderExactlyOnce() {
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        conversionService.convert("USD", "EUR", new BigDecimal("100"));

        verify(exchangeRateProvider, times(1)).getRateDetails("USD", "EUR");
    }

    // -------------------------------------------------------------------------
    // Factory helper
    // -------------------------------------------------------------------------

    /**
     * Stubs the provider to return a fixed rate for the given currency pair.
     */
    private void mockRate(String from, String to, BigDecimal rate) {
        ExchangeRateResponse rateResponse = new ExchangeRateResponse(from, to, rate, LocalDate.now());
        when(exchangeRateProvider.getRateDetails(from, to)).thenReturn(rateResponse);
    }
}

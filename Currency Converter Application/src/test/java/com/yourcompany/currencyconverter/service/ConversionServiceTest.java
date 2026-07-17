package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.ExternalApiException;
import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.model.entity.ConversionHistory;
import com.yourcompany.currencyconverter.repository.ConversionHistoryRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ConversionService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConversionService – Unit Tests")
class ConversionServiceTest {

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ConversionHistoryRepository historyRepository;

    @InjectMocks
    private ConversionService conversionService;

    @BeforeEach
    void setUpCurrencyValidation() {
        // Default: all currency codes exist in the database and return a mock Currency entity.
        // Individual tests override this for error-case scenarios.
        lenient().when(currencyRepository.findById(anyString()))
                .thenAnswer(invocation -> {
                    String code = invocation.getArgument(0);
                    return Optional.of(new Currency(code, code + " Name", "$"));
                });
    }

    // -------------------------------------------------------------------------
    // Primary acceptance-criteria test
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("convert(USD, EUR, 100) with rate 0.85 returns result 85.0000 and saves history")
    void convert_usdToEur_100units_rate085_returns85() {
        // Arrange
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        // Act
        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("100"));

        // Assert
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("85.0000"));
        
        // Verify history was saved
        verify(historyRepository, times(1)).save(any(ConversionHistory.class));
    }

    // -------------------------------------------------------------------------
    // Full response DTO field validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("convert() populates all ConversionResponse fields correctly")
    void convert_populatesAllResponseFields() {
        LocalDateTime before = LocalDateTime.now();
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("250.00"));

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
        mockRate("GBP", "JPY", new BigDecimal("198.53"));
        LocalDateTime before = LocalDateTime.now();

        ConversionResponse response = conversionService.convert("GBP", "JPY", new BigDecimal("10"));

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
        mockRate("USD", "EUR", new BigDecimal("0.8542"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("100"));

        assertThat(response.getResult().scale()).isEqualTo(4);
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("85.4200"));
    }

    @Test
    @DisplayName("HALF_UP rounding: 1 × 0.33335 rounds up to 0.3334")
    void convert_halfUpRounding_roundsCorrectly() {
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
        mockRate("USD", "EUR", new BigDecimal("0.85"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("0.01"));

        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("0.0085"));
    }

    @Test
    @DisplayName("Large amount (1,000,000) retains full precision")
    void convert_largeAmount_retainsFullPrecision() {
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
        when(currencyRepository.findById("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert("XYZ", "EUR", new BigDecimal("100")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("XYZ");

        verifyNoInteractions(exchangeRateProvider);
        verifyNoInteractions(historyRepository);
    }

    @Test
    @DisplayName("Unsupported 'to' currency throws ResourceNotFoundException")
    void convert_unsupportedToCurrency_throwsResourceNotFoundException() {
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(new Currency("USD", "US Dollar", "$")));
        when(currencyRepository.findById("ZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert("USD", "ZZZ", new BigDecimal("100")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ZZZ");

        verifyNoInteractions(exchangeRateProvider);
        verifyNoInteractions(historyRepository);
    }

    @Test
    @DisplayName("'from' is validated first — 'to' is not checked if 'from' is invalid")
    void convert_fromValidatedBeforeTo() {
        when(currencyRepository.findById("BAD")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert("BAD", "EUR", BigDecimal.TEN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BAD");

        verify(currencyRepository, never()).findById("EUR");
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

    private void mockRate(String from, String to, BigDecimal rate) {
        ExchangeRateResponse rateResponse = new ExchangeRateResponse(from, to, rate, LocalDate.now());
        when(exchangeRateProvider.getRateDetails(from, to)).thenReturn(rateResponse);
    }
}

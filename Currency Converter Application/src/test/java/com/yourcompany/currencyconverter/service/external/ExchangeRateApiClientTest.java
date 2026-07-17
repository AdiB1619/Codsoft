package com.yourcompany.currencyconverter.service.external;

import com.yourcompany.currencyconverter.exception.ExternalApiException;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link ExchangeRateApiClient} using Mockito.
 *
 * <p>No Spring context is loaded — the {@link RestTemplate} is mocked directly,
 * making these tests fast, isolated, and runnable completely offline.
 *
 * <p>The test class lives in the same package as the client so it can access
 * the package-private inner DTO class {@code ExchangeRateApiClient.ApiPairResponse}
 * and set its fields directly to simulate various API responses.
 *
 * <p>Test scope:
 * <ul>
 *   <li>Happy path: rate extracted correctly from a success response.</li>
 *   <li>Full DTO populated: all fields of {@link ExchangeRateResponse} are correct.</li>
 *   <li>Same-currency pair: rate 1.0 returned correctly.</li>
 *   <li>Unknown JSON fields: the client ignores extra fields gracefully.</li>
 *   <li>API returns {@code "result":"error"}: {@link ExternalApiException} is thrown.</li>
 *   <li>Null response body: {@link ExternalApiException} is thrown.</li>
 *   <li>Null conversion_rate field: {@link ExternalApiException} is thrown.</li>
 *   <li>HTTP 500: client retries exactly once, then throws {@link ExternalApiException}.</li>
 *   <li>Retry count: exactly {@code MAX_ATTEMPTS} calls are made on repeated failures.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeRateApiClient – Unit Tests (Mockito)")
class ExchangeRateApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ExchangeRateApiClient client;

    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6";
    private static final String TEST_KEY  = "TEST_KEY";

    @BeforeEach
    void setUp() {
        // Construct client with mocked RestTemplate and test config values.
        client = new ExchangeRateApiClient(restTemplate, BASE_URL, TEST_KEY);
    }

    // -------------------------------------------------------------------------
    // Happy-path tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getExchangeRate returns correct BigDecimal rate from a success response")
    void getExchangeRate_successfulResponse_returnsCorrectRate() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/EUR";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(successResponse("USD", "EUR", new BigDecimal("0.8542")));

        // Act
        BigDecimal rate = client.getExchangeRate("USD", "EUR");

        // Assert
        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.8542"));
        verify(restTemplate, times(1)).getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class);
    }

    @Test
    @DisplayName("getRateDetails returns fully populated ExchangeRateResponse DTO")
    void getRateDetails_successfulResponse_returnsFullDto() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/GBP/JPY";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(successResponse("GBP", "JPY", new BigDecimal("198.5300")));

        // Act
        ExchangeRateResponse response = client.getRateDetails("GBP", "JPY");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFrom()).isEqualTo("GBP");
        assertThat(response.getTo()).isEqualTo("JPY");
        assertThat(response.getRate()).isEqualByComparingTo(new BigDecimal("198.5300"));
        assertThat(response.getRateDate()).isNotNull();
    }

    @Test
    @DisplayName("getExchangeRate with same-currency pair returns rate of 1.0")
    void getExchangeRate_sameCurrencyPair_returnsOne() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/USD";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(successResponse("USD", "USD", BigDecimal.ONE));

        // Act
        BigDecimal rate = client.getExchangeRate("USD", "USD");

        // Assert
        assertThat(rate).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("getRateDetails uses fallback currency codes when API omits base/target fields")
    void getRateDetails_missingCodeFields_fallsBackToArguments() {
        // Arrange – response with null base_code and target_code (edge case)
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/INR/AED";
        ExchangeRateApiClient.ApiPairResponse response = new ExchangeRateApiClient.ApiPairResponse();
        response.result = "success";
        response.baseCode = null;    // API omitted the field
        response.targetCode = null;
        response.conversionRate = new BigDecimal("0.04312");

        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(response);

        // Act
        ExchangeRateResponse dto = client.getRateDetails("INR", "AED");

        // Assert — should fall back to the method arguments
        assertThat(dto.getFrom()).isEqualTo("INR");
        assertThat(dto.getTo()).isEqualTo("AED");
        assertThat(dto.getRate()).isEqualByComparingTo(new BigDecimal("0.04312"));
    }

    @Test
    @DisplayName("getRateDetails uses open.er-api.com fallback when API key is 'YOUR_API_KEY_HERE'")
    void getRateDetails_withDefaultApiKey_usesFallback() {
        // Arrange
        ExchangeRateApiClient clientWithDefaultKey = new ExchangeRateApiClient(restTemplate, BASE_URL, "YOUR_API_KEY_HERE");
        
        String expectedUrl = "https://open.er-api.com/v6/latest/USD";
        ExchangeRateApiClient.OpenApiResponse openResponse = new ExchangeRateApiClient.OpenApiResponse();
        openResponse.result = "success";
        openResponse.baseCode = "USD";
        openResponse.rates = java.util.Map.of("EUR", new BigDecimal("0.85"));

        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.OpenApiResponse.class))
                .thenReturn(openResponse);

        // Act
        ExchangeRateResponse response = clientWithDefaultKey.getRateDetails("USD", "EUR");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getRate()).isEqualByComparingTo(new BigDecimal("0.85"));
        verify(restTemplate, times(1)).getForObject(expectedUrl, ExchangeRateApiClient.OpenApiResponse.class);
    }

    // -------------------------------------------------------------------------
    // Error handling tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("API response with result='error' throws ExternalApiException")
    void getExchangeRate_apiReturnsErrorResult_throwsExternalApiException() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/XYZ";
        ExchangeRateApiClient.ApiPairResponse errorResponse = new ExchangeRateApiClient.ApiPairResponse();
        errorResponse.result    = "error";
        errorResponse.errorType = "unsupported-code";

        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(errorResponse);

        // Act + Assert
        assertThatThrownBy(() -> client.getExchangeRate("USD", "XYZ"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("USD/XYZ");
    }

    @Test
    @DisplayName("Null API response body throws ExternalApiException")
    void getExchangeRate_nullResponse_throwsExternalApiException() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/EUR";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(null);

        // Act + Assert
        assertThatThrownBy(() -> client.getExchangeRate("USD", "EUR"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("empty response");
    }

    @Test
    @DisplayName("Null conversion_rate in success response throws ExternalApiException")
    void getExchangeRate_nullConversionRate_throwsExternalApiException() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/EUR";
        ExchangeRateApiClient.ApiPairResponse response = new ExchangeRateApiClient.ApiPairResponse();
        response.result         = "success";
        response.baseCode       = "USD";
        response.targetCode     = "EUR";
        response.conversionRate = null; // missing field

        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenReturn(response);

        // Act + Assert
        assertThatThrownBy(() -> client.getExchangeRate("USD", "EUR"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("conversion_rate");
    }

    // -------------------------------------------------------------------------
    // Retry logic tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("On RestClientException the client retries exactly once, then throws ExternalApiException")
    void getExchangeRate_restClientException_retriesOnceAndThrows() {
        // Arrange — both attempts throw a network error
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/USD/EUR";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenThrow(new RestClientException("Connection refused"));

        // Act + Assert
        assertThatThrownBy(() -> client.getExchangeRate("USD", "EUR"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("unavailable");

        // Verify retry happened: RestTemplate called exactly MAX_ATTEMPTS (2) times
        verify(restTemplate, times(2))
                .getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class);
    }

    @Test
    @DisplayName("On HTTP 500 the client retries once and throws ExternalApiException")
    void getExchangeRate_http500_retriesAndThrows() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/CAD/MXN";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenThrow(new HttpServerErrorException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error"));

        // Act + Assert
        assertThatThrownBy(() -> client.getExchangeRate("CAD", "MXN"))
                .isInstanceOf(ExternalApiException.class);

        verify(restTemplate, times(2))
                .getForObject(anyString(), eq(ExchangeRateApiClient.ApiPairResponse.class));
    }

    @Test
    @DisplayName("Succeeds on second attempt (first attempt fails, retry succeeds)")
    void getExchangeRate_firstAttemptFails_secondAttemptSucceeds() {
        // Arrange
        String expectedUrl = BASE_URL + "/" + TEST_KEY + "/pair/EUR/CHF";
        when(restTemplate.getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class))
                .thenThrow(new RestClientException("Timeout"))         // first call throws
                .thenReturn(successResponse("EUR", "CHF", new BigDecimal("0.9650")));  // retry succeeds

        // Act
        BigDecimal rate = client.getExchangeRate("EUR", "CHF");

        // Assert — should return the rate from the second attempt
        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.9650"));
        verify(restTemplate, times(2))
                .getForObject(expectedUrl, ExchangeRateApiClient.ApiPairResponse.class);
    }

    // -------------------------------------------------------------------------
    // Factory helper
    // -------------------------------------------------------------------------

    /**
     * Builds a success {@link ExchangeRateApiClient.ApiPairResponse} with the given values.
     * Extracted to reduce boilerplate in each test method.
     */
    private ExchangeRateApiClient.ApiPairResponse successResponse(
            String from, String to, BigDecimal rate) {
        ExchangeRateApiClient.ApiPairResponse r = new ExchangeRateApiClient.ApiPairResponse();
        r.result         = "success";
        r.baseCode       = from;
        r.targetCode     = to;
        r.conversionRate = rate;
        return r;
    }
}

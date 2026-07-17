package com.yourcompany.currencyconverter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.currencyconverter.exception.ExternalApiException;
import com.yourcompany.currencyconverter.exception.GlobalExceptionHandler;
import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.service.ConversionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for {@link ConversionController} using {@link MockMvc}.
 *
 * <p>{@code @WebMvcTest} loads only the web layer (controller + MVC config).
 * {@link ConversionService} is replaced with a {@code @MockBean} — no database,
 * no HTTP calls, no full Spring context.
 *
 * <p>{@code @Import(GlobalExceptionHandler.class)} ensures the exception handler
 * is active in the test slice so HTTP status codes and error bodies are verified
 * exactly as they would be in production.
 *
 * <p>Covers:
 * <ul>
 *   <li>POST /api/convert – happy path: returns 200 with correct JSON fields.</li>
 *   <li>POST /api/convert – invalid JSON (blank from/to, bad pattern, zero amount): 400.</li>
 *   <li>POST /api/convert – missing request body: 400.</li>
 *   <li>POST /api/convert – unsupported currency: 404.</li>
 *   <li>POST /api/convert – external API failure: 502.</li>
 *   <li>GET  /api/currencies – returns list with 200.</li>
 *   <li>GET  /api/currencies – empty list still returns 200.</li>
 * </ul>
 */
@WebMvcTest(ConversionController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ConversionController – MockMvc Tests")
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConversionService conversionService;

    // -------------------------------------------------------------------------
    // POST /api/convert – Happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/convert with valid request returns 200 and correct JSON")
    void convert_validRequest_returns200WithCorrectBody() throws Exception {
        // Arrange
        ConversionResponse mockResponse = new ConversionResponse(
                "USD", "EUR",
                new BigDecimal("100"),
                new BigDecimal("85.0000"),
                new BigDecimal("0.850000"),
                LocalDateTime.of(2026, 7, 17, 18, 34, 10));

        when(conversionService.convert(any(ConversionRequest.class))).thenReturn(mockResponse);

        String requestJson = """
                {
                  "from":   "USD",
                  "to":     "EUR",
                  "amount": 100
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.result").value(85.0000))
                .andExpect(jsonPath("$.rate").value(0.850000))
                .andExpect(jsonPath("$.timestamp").value("2026-07-17T18:34:10"));

        verify(conversionService, times(1)).convert(any(ConversionRequest.class));
    }

    @Test
    @DisplayName("POST /api/convert with large amount returns 200")
    void convert_largeAmount_returns200() throws Exception {
        ConversionResponse mockResponse = new ConversionResponse(
                "USD", "JPY",
                new BigDecimal("1000000"),
                new BigDecimal("155000000.0000"),
                new BigDecimal("155.000000"),
                LocalDateTime.now());

        when(conversionService.convert(any(ConversionRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"USD","to":"JPY","amount":1000000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("JPY"));
    }

    // -------------------------------------------------------------------------
    // POST /api/convert – Validation failures (400)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/convert with blank 'from' returns 400 with field error")
    void convert_blankFrom_returns400() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"","to":"EUR","amount":100}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.from").exists());

        verifyNoInteractions(conversionService);
    }

    @Test
    @DisplayName("POST /api/convert with lowercase 'from' code returns 400 (pattern violation)")
    void convert_lowercaseFromCode_returns400() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"usd","to":"EUR","amount":100}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.from").exists());
    }

    @Test
    @DisplayName("POST /api/convert with zero amount returns 400")
    void convert_zeroAmount_returns400() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"USD","to":"EUR","amount":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    @Test
    @DisplayName("POST /api/convert with negative amount returns 400")
    void convert_negativeAmount_returns400() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"USD","to":"EUR","amount":-50}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    @Test
    @DisplayName("POST /api/convert with multiple invalid fields returns 400 with all errors")
    void convert_multipleInvalidFields_returns400WithAllErrors() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"","to":"bad","amount":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.from").exists())
                .andExpect(jsonPath("$.fieldErrors.to").exists())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    @Test
    @DisplayName("POST /api/convert with missing body returns 400")
    void convert_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /api/convert – Business rule failures
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/convert with unsupported currency returns 404")
    void convert_unsupportedCurrency_returns404() throws Exception {
        when(conversionService.convert(any(ConversionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Currency not supported: 'XYZ'"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"XYZ","to":"EUR","amount":100}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("XYZ")));
    }

    @Test
    @DisplayName("POST /api/convert when external API is down returns 502")
    void convert_externalApiDown_returns502() throws Exception {
        when(conversionService.convert(any(ConversionRequest.class)))
                .thenThrow(new ExternalApiException("API timeout"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"USD","to":"EUR","amount":100}
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value(containsString("unavailable")));
    }

    // -------------------------------------------------------------------------
    // GET /api/currencies
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/currencies returns 200 with all currencies")
    void getAllCurrencies_returns200WithList() throws Exception {
        List<Currency> currencies = List.of(
                new Currency("USD", "US Dollar", "$"),
                new Currency("EUR", "Euro", "€"),
                new Currency("JPY", "Japanese Yen", "¥"));

        when(conversionService.getAllCurrencies()).thenReturn(currencies);

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].code").value("USD"))
                .andExpect(jsonPath("$[1].code").value("EUR"))
                .andExpect(jsonPath("$[2].code").value("JPY"));
    }

    @Test
    @DisplayName("GET /api/currencies with empty database returns 200 with empty array")
    void getAllCurrencies_emptyDatabase_returns200WithEmptyArray() throws Exception {
        when(conversionService.getAllCurrencies()).thenReturn(List.of());

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

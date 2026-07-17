package com.yourcompany.currencyconverter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import com.yourcompany.currencyconverter.service.external.ExchangeRateProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConversionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We mock the external API provider so we don't make real network calls in the IT
    @MockBean
    private ExchangeRateProvider exchangeRateProvider;

    @Test
    void convert_ValidRequest_ReturnsSuccessfulResponse() throws Exception {
        // Arrange
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("100"));

        ExchangeRateResponse mockRateResponse = new ExchangeRateResponse(
                "USD", "EUR", new BigDecimal("0.85"), LocalDate.now());

        when(exchangeRateProvider.getRateDetails("USD", "EUR")).thenReturn(mockRateResponse);

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.rate").value(0.85))
                .andExpect(jsonPath("$.result").value(85.00));
    }

    @Test
    void convert_IdenticalCurrencies_ShortCircuitsAndReturnsRateOne() throws Exception {
        // Arrange
        ConversionRequest request = new ConversionRequest("USD", "USD", new BigDecimal("500"));

        // Note: We don't mock exchangeRateProvider here because the service should short-circuit
        // and NEVER call the provider if from == to.

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("USD"))
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.rate").value(1.0))
                .andExpect(jsonPath("$.result").value(500.00));
    }

    @Test
    void convert_InvalidCurrencyCode_Returns404NotFound() throws Exception {
        // Arrange
        ConversionRequest request = new ConversionRequest("XXX", "EUR", new BigDecimal("100"));

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Currency not supported: 'XXX'. Please use a currency from the /api/currencies endpoint."));
    }
}

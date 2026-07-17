package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.dto.ConversionHistoryDto;
import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.dto.ExchangeRateResponse;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.model.entity.ConversionHistory;
import com.yourcompany.currencyconverter.repository.ConversionHistoryRepository;
import com.yourcompany.currencyconverter.repository.CurrencyRepository;
import com.yourcompany.currencyconverter.service.external.ExchangeRateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
 *   <li><strong>Persist</strong> – save the conversion result to the database
 *       via {@link ConversionHistoryRepository}.</li>
 *   <li><strong>Return</strong> – package the result into an immutable
 *       {@link ConversionResponse} DTO with a server-side timestamp.</li>
 * </ol>
 */
@Service
public class ConversionService {

    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);

    private static final int RESULT_SCALE = 4;

    private final ExchangeRateProvider exchangeRateProvider;
    private final CurrencyRepository currencyRepository;
    private final ConversionHistoryRepository historyRepository;

    public ConversionService(ExchangeRateProvider exchangeRateProvider,
                             CurrencyRepository currencyRepository,
                             ConversionHistoryRepository historyRepository) {
        this.exchangeRateProvider = exchangeRateProvider;
        this.currencyRepository   = currencyRepository;
        this.historyRepository    = historyRepository;
    }

    /**
     * Converts {@code amount} of {@code from} currency into {@code to} currency.
     */
    @Transactional
    public ConversionResponse convert(String from, String to, BigDecimal amount) {
        log.info("Converting {} {} → {}", amount, from, to);

        // Validate and retrieve full Currency entities
        Currency fromCurrency = getValidCurrency(from);
        Currency toCurrency = getValidCurrency(to);

        ExchangeRateResponse rateDetails = exchangeRateProvider.getRateDetails(from, to);
        BigDecimal rate = rateDetails.getRate();
        log.debug("Rate fetched: 1 {} = {} {}", from, rate, to);

        BigDecimal result = amount
                .multiply(rate)
                .setScale(RESULT_SCALE, RoundingMode.HALF_UP);

        log.info("Conversion result: {} {} = {} {}", amount, from, result, to);

        LocalDateTime now = LocalDateTime.now();

        ConversionHistory history = new ConversionHistory(fromCurrency, toCurrency, amount, result, rate, now);
        historyRepository.save(history);

        return new ConversionResponse(from, to, amount, result, rate, now);
    }

    public ConversionResponse convert(ConversionRequest request) {
        return convert(request.getFrom(), request.getTo(), request.getAmount());
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    
    /**
     * Retrieves the conversion history, returning DTOs to hide internal entity details.
     */
    @Transactional(readOnly = true)
    public List<ConversionHistoryDto> getHistory() {
        return historyRepository.findAllByOrderByConvertedAtDesc()
                .stream()
                .map(entity -> new ConversionHistoryDto(
                        entity.getId(),
                        entity.getSourceCurrency().getCode(),
                        entity.getTargetCurrency().getCode(),
                        entity.getAmount(),
                        entity.getResult(),
                        entity.getRate(),
                        entity.getConvertedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a specific history record by its ID.
     */
    @Transactional
    public void deleteHistory(Long id) {
        if (!historyRepository.existsById(id)) {
            throw new ResourceNotFoundException("History record not found with id: " + id);
        }
        historyRepository.deleteById(id);
    }

    /**
     * Retrieves the Currency entity or throws an exception.
     */
    private Currency getValidCurrency(String code) {
        return currencyRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Currency not supported: '" + code + "'. "
                        + "Please use a currency from the /api/currencies endpoint."));
    }
}

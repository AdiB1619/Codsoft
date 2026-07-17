package com.yourcompany.currencyconverter.controller;

import com.yourcompany.currencyconverter.model.dto.ConversionRequest;
import com.yourcompany.currencyconverter.model.dto.ConversionResponse;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.service.ConversionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing the currency conversion API.
 *
 * <h2>Base path: {@code /api}</h2>
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li>{@code POST /api/convert}    – Convert an amount from one currency to another.</li>
 *   <li>{@code GET  /api/currencies} – List all supported ISO 4217 currency codes.</li>
 * </ul>
 *
 * <h2>Design constraints</h2>
 * <ul>
 *   <li>All business logic lives in {@link ConversionService} — this controller
 *       only validates input and delegates.</li>
 *   <li>{@code @Valid} on the request body triggers Bean Validation. Any
 *       constraint violations throw {@code MethodArgumentNotValidException},
 *       which {@code GlobalExceptionHandler} maps to HTTP 400 with field details.</li>
 *   <li>No try/catch blocks here — exceptions bubble up to {@code GlobalExceptionHandler}.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class ConversionController {

    private static final Logger log = LoggerFactory.getLogger(ConversionController.class);

    private final ConversionService conversionService;

    // -------------------------------------------------------------------------
    // Constructor injection
    // -------------------------------------------------------------------------

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    // -------------------------------------------------------------------------
    // POST /api/convert
    // -------------------------------------------------------------------------

    /**
     * Converts {@code amount} of the source currency to the target currency.
     *
     * <h3>Request</h3>
     * <pre>{@code
     * POST /api/convert
     * Content-Type: application/json
     *
     * {
     *   "from":   "USD",
     *   "to":     "EUR",
     *   "amount": 100
     * }
     * }</pre>
     *
     * <h3>Success response (200 OK)</h3>
     * <pre>{@code
     * {
     *   "from":      "USD",
     *   "to":        "EUR",
     *   "amount":    100,
     *   "result":    85.4200,
     *   "rate":      0.854200,
     *   "timestamp": "2026-07-17T18:34:10"
     * }
     * }</pre>
     *
     * <h3>Error responses</h3>
     * <ul>
     *   <li>400 – validation failure (blank code, wrong format, amount ≤ 0)</li>
     *   <li>404 – currency code not in the supported list</li>
     *   <li>502 – exchange-rate API unreachable</li>
     * </ul>
     *
     * @param request validated conversion request body
     * @return 200 OK with {@link ConversionResponse}
     */
    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(
            @Valid @RequestBody ConversionRequest request) {

        log.info("POST /api/convert – {} {} → {}", request.getAmount(), request.getFrom(), request.getTo());
        ConversionResponse response = conversionService.convert(request);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // GET /api/currencies
    // -------------------------------------------------------------------------

    /**
     * Returns the list of all supported ISO 4217 currency codes seeded into the database.
     *
     * <p>Clients should call this endpoint to discover valid values for the
     * {@code from} and {@code to} fields of {@code POST /api/convert}.
     *
     * <h3>Success response (200 OK)</h3>
     * <pre>{@code
     * [
     *   { "code": "USD", "name": "US Dollar",  "symbol": "$"  },
     *   { "code": "EUR", "name": "Euro",        "symbol": "€"  },
     *   ...
     * ]
     * }</pre>
     *
     * @return 200 OK with list of all supported {@link Currency} objects
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        log.debug("GET /api/currencies");
        List<Currency> currencies = conversionService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }
}

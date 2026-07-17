package com.yourcompany.currencyconverter.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardised error response body returned by {@code GlobalExceptionHandler}
 * for all 4xx and 5xx HTTP responses.
 *
 * <p>Having a single, predictable error shape means API clients can write
 * one error-handling routine instead of one per endpoint.
 *
 * <h2>Sample 400 response (validation failure)</h2>
 * <pre>{@code
 * {
 *   "status":    400,
 *   "error":     "Bad Request",
 *   "message":   "Validation failed for 2 field(s)",
 *   "timestamp": "2026-07-17T18:34:10",
 *   "fieldErrors": {
 *     "from":   "Source currency must be a valid 3-letter ISO 4217 code (e.g. USD)",
 *     "amount": "Amount must be greater than zero (minimum 0.01)"
 *   }
 * }
 * }</pre>
 *
 * <h2>Sample 404 response (unknown currency)</h2>
 * <pre>{@code
 * {
 *   "status":    404,
 *   "error":     "Not Found",
 *   "message":   "Currency not supported: 'XYZ'.",
 *   "timestamp": "2026-07-17T18:34:10"
 * }
 * }</pre>
 *
 * <p>{@code @JsonInclude(NON_NULL)} suppresses the {@code fieldErrors} field on
 * non-validation errors so the response stays clean.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** HTTP status code (mirrors the response status line). */
    private final int status;

    /** Short HTTP reason phrase (e.g. "Bad Request", "Not Found"). */
    private final String error;

    /** Human-readable description of the problem. */
    private final String message;

    /** Server time when the error occurred (ISO-8601). */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    /**
     * Per-field validation messages, populated only for HTTP 400 responses.
     * {@code null} for all other error types (suppressed in JSON by {@code @JsonInclude}).
     */
    private final Map<String, String> fieldErrors;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Constructor for non-validation errors (no field-level detail). */
    public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
        this(status, error, message, timestamp, null);
    }

    /** Constructor for validation errors (includes field-level detail map). */
    public ErrorResponse(int status, String error, String message,
                         LocalDateTime timestamp, Map<String, String> fieldErrors) {
        this.status      = status;
        this.error       = error;
        this.message     = message;
        this.timestamp   = timestamp;
        this.fieldErrors = fieldErrors;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public int getStatus()                    { return status;      }
    public String getError()                  { return error;       }
    public String getMessage()                { return message;     }
    public LocalDateTime getTimestamp()       { return timestamp;   }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}

package com.codsoft.sms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardised error response payload across all API endpoints.
 *
 * <p>Returned by {@link com.codsoft.sms.exception.GlobalExceptionHandler} whenever
 * an exception occurs, ensuring clients always receive a predictable JSON shape
 * for errors, as defined in SDD Section 11.2.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorResponse {

    /** Always {@code false} for error responses. */
    @Builder.Default
    private final boolean success = false;

    /** The HTTP status code (e.g. 404, 409). */
    private final int status;

    /** The HTTP reason phrase (e.g. "Not Found", "Conflict"). */
    private final String error;

    /** Human-readable error message explaining the issue. */
    private final String message;

    /** The request URI that triggered the error. */
    private final String path;

    /** ISO-8601 timestamp of when the error occurred. */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * List of field-specific validation errors.
     * Only populated for 400 Bad Request responses caused by Bean Validation failures;
     * omitted (or empty) for other error types.
     */
    private final List<FieldError> fieldErrors;

    /**
     * Represents a single field validation error.
     */
    @Getter
    @Builder
    public static final class FieldError {
        private final String field;
        private final String message;
    }
}

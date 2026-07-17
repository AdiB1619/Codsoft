package com.yourcompany.currencyconverter.exception;

import com.yourcompany.currencyconverter.model.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Centralised exception handler for the entire REST API.
 *
 * <p>Using {@link RestControllerAdvice} means this handler intercepts exceptions
 * thrown by any {@code @RestController} and returns a structured
 * {@link ErrorResponse} JSON body instead of Spring's default error page.
 *
 * <h2>Handled exceptions and their HTTP status codes</h2>
 * <ul>
 *   <li>{@link HttpMessageNotReadableException}    → <strong>400 Bad Request</strong>
 *       (missing or malformed JSON request body).</li>
 *   <li>{@link MethodArgumentNotValidException}    → <strong>400 Bad Request</strong>
 *       (Bean Validation failures on {@code @Valid @RequestBody}).</li>
 *   <li>{@link ResourceNotFoundException}          → <strong>404 Not Found</strong>
 *       (e.g. unsupported currency code).</li>
 *   <li>{@link ExternalApiException}               → <strong>502 Bad Gateway</strong>
 *       (external exchange-rate API unavailable or returning an error).</li>
 *   <li>{@link Exception} (catch-all)              → <strong>500 Internal Server Error</strong>
 *       (any unhandled exception; root cause is logged but not exposed to clients).</li>
 * </ul>
 *
 * <p>All responses share the same {@link ErrorResponse} shape, making client-side
 * error handling uniform regardless of which error occurred.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------------------------------------------------------------
    // 400 Bad Request — missing or malformed JSON body
    // -------------------------------------------------------------------------

    /**
     * Handles {@link HttpMessageNotReadableException} thrown when the request
     * body is absent or contains invalid JSON that cannot be parsed into the
     * expected type.
     *
     * @param ex the message-not-readable exception
     * @return 400 response with a clear description
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Request body missing or malformed: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Request body is missing or contains invalid JSON",
                        LocalDateTime.now()));
    }

    // -------------------------------------------------------------------------
    // 400 Bad Request — Bean Validation failure
    // -------------------------------------------------------------------------

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when a
     * {@code @Valid @RequestBody} fails Bean Validation constraints.
     *
     * <p>Collects <strong>all</strong> field-level violations into a map so
     * the client receives every problem in a single response rather than
     * having to fix one field at a time (fail-fast is fine for small forms;
     * fail-all is better for APIs).
     *
     * @param ex the validation exception thrown by Spring MVC
     * @return 400 response with field-to-message error map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        String message = "Validation failed for " + fieldErrors.size() + " field(s)";
        log.warn("Validation failure: {}", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        message,
                        LocalDateTime.now(),
                        fieldErrors));
    }

    // -------------------------------------------------------------------------
    // 404 Not Found — resource missing
    // -------------------------------------------------------------------------

    /**
     * Handles {@link ResourceNotFoundException} (e.g. unsupported currency code,
     * missing conversion history record).
     *
     * @param ex the not-found exception
     * @return 404 response with the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()));
    }

    // -------------------------------------------------------------------------
    // 502 Bad Gateway — external API failure
    // -------------------------------------------------------------------------

    /**
     * Handles {@link ExternalApiException} when the upstream exchange-rate
     * provider is unavailable or returns an error response.
     *
     * <p>HTTP 502 ("Bad Gateway") is the correct status for "I tried to call
     * an upstream service and it failed", distinguishing it from our own 500s.
     *
     * @param ex the external API exception
     * @return 502 response with a safe, user-friendly message
     */
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException ex) {
        log.error("External API failure: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(
                        HttpStatus.BAD_GATEWAY.value(),
                        HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                        "Exchange rate service is currently unavailable. Please try again later.",
                        LocalDateTime.now()));
    }

    // -------------------------------------------------------------------------
    // 500 Internal Server Error — catch-all
    // -------------------------------------------------------------------------

    /**
     * Safety net for any exception not handled by a more specific handler above.
     *
     * <p>The root cause is logged at ERROR level for server-side diagnosis,
     * but the response contains only a generic message to avoid leaking
     * internal implementation details (e.g. stack traces, SQL errors) to clients.
     *
     * @param ex the unhandled exception
     * @return 500 response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()));
    }
}

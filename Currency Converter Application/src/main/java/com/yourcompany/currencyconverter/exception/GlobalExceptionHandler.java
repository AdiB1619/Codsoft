package com.yourcompany.currencyconverter.exception;

import com.yourcompany.currencyconverter.model.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

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
 *   <li>{@link DuplicateResourceException}         → <strong>409 Conflict</strong>
 *       (e.g. favorite currency already exists).</li>
 *   <li>{@link ExternalApiException}               → <strong>502 Bad Gateway</strong>
 *       (external exchange-rate API unavailable or returning an error).</li>
 *   <li>{@link Exception} (catch-all)              → <strong>500 Internal Server Error</strong>
 *       (any unhandled exception; root cause is logged but not exposed to clients).</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Request body missing or malformed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is missing or contains invalid JSON");
    }

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        log.info("Duplicate resource: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException ex) {
        log.error("External API failure: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "Exchange rate service is currently unavailable. Please try again later.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, LocalDateTime.now()));
    }
}

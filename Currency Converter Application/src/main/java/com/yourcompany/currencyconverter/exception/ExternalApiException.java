package com.yourcompany.currencyconverter.exception;

/**
 * Thrown when the external exchange-rate API is unreachable, times out,
 * or returns an unexpected/error response.
 *
 * <p>This is a runtime exception (unchecked) so callers are not forced to
 * declare it in their signatures, but they should still handle it through
 * the global {@code @ControllerAdvice} exception handler which maps it to
 * HTTP 502 Bad Gateway.
 *
 * <p>Design note: wrapping the root cause lets the global handler log
 * the original exception while returning a user-friendly message to the client.
 *
 * @see com.yourcompany.currencyconverter.exception.ResourceNotFoundException
 */
public class ExternalApiException extends RuntimeException {

    /**
     * Constructs an ExternalApiException with a descriptive message.
     *
     * @param message human-readable description of the failure
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * Constructs an ExternalApiException wrapping a root cause.
     *
     * @param message human-readable description of the failure
     * @param cause   the underlying exception (e.g. {@link org.springframework.web.client.RestClientException})
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

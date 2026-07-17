package com.yourcompany.currencyconverter.exception;

/**
 * Thrown when a requested resource cannot be found in the database or system.
 *
 * <p>Examples of when this is thrown:
 * <ul>
 *   <li>A currency code passed to {@code ConversionService} is not present
 *       in the {@code currencies} table.</li>
 *   <li>A conversion history record ID does not exist.</li>
 *   <li>A favourite currency pair is not found.</li>
 * </ul>
 *
 * <p>This is an unchecked (runtime) exception. The global
 * {@code GlobalExceptionHandler} maps it to HTTP 404 Not Found with a
 * user-friendly error message, so callers do not need to declare it in
 * their method signatures.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a ResourceNotFoundException with a descriptive message.
     *
     * @param message description of the missing resource (e.g. "Currency not supported: XYZ")
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a ResourceNotFoundException wrapping a root cause.
     *
     * @param message description of the missing resource
     * @param cause   the underlying exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

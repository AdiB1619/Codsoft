package com.codsoft.sms.exception;

/**
 * Thrown when an uploaded file fails validation (e.g. wrong MIME type, exceeds size limit).
 *
 * <p>Mapped to {@code 400 Bad Request} by {@link GlobalExceptionHandler}.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}

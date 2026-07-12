package com.codsoft.sms.exception;

/**
 * Thrown when the server fails to read, write, or persist an uploaded file.
 *
 * <p>Mapped to {@code 500 Internal Server Error} by {@link GlobalExceptionHandler}.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

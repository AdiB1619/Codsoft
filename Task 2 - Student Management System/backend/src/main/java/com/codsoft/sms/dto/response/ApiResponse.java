package com.codsoft.sms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard envelope wrapping every non-export, non-paginated API response.
 *
 * <p>Shape per SDD Section 7.1:
 * <pre>{@code
 * {
 *   "success":   true,
 *   "message":   "Human-readable summary",
 *   "data":      { ... },
 *   "timestamp": "2026-07-07T10:15:30"
 * }
 * }</pre>
 *
 * <p>Designed as an immutable value object — use the static factory methods
 * {@link #success(String, T)} and {@link #error(String)} rather than the
 * constructor directly.
 *
 * @param <T> the type of the {@code data} payload
 */
@Getter
public final class ApiResponse<T> {

    /** Whether the request succeeded. */
    private final boolean success;

    /** Human-readable summary of the outcome. */
    private final String message;

    /**
     * The response payload. {@code null} on error responses.
     * Jackson omits this field when null (configured via
     * {@code spring.jackson.default-property-inclusion=non_null}).
     */
    private final T data;

    /** Server-side timestamp of when this response was produced. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a success response wrapping the given payload.
     *
     * @param message a short, human-readable description of what succeeded
     * @param data    the response payload
     * @param <T>     the payload type
     * @return a populated success {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error response with no payload.
     *
     * <p>Prefer using {@code ErrorResponse} for structured error details;
     * this factory is provided for simple cases where only a message is needed.
     *
     * @param message a short, human-readable description of what failed
     * @param <T>     the phantom payload type
     * @return a populated error {@link ApiResponse} with {@code data = null}
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

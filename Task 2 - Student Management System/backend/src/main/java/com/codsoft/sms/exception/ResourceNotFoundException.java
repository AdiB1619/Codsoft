package com.codsoft.sms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource cannot be found in the database.
 *
 * <p>Maps to HTTP {@code 404 Not Found}.
 *
 * <p>Examples:
 * <ul>
 *   <li>No student with the given ID exists</li>
 *   <li>No course with the given ID or code exists</li>
 * </ul>
 *
 * <p>The {@link com.codsoft.sms.exception.GlobalExceptionHandler} (Prompt 10)
 * intercepts this exception and returns a structured {@code ErrorResponse}.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a {@link ResourceNotFoundException} with a descriptive message.
     *
     * @param resourceName the type of resource (e.g. {@code "Student"}, {@code "Course"})
     * @param fieldName    the field used for lookup (e.g. {@code "id"}, {@code "courseCode"})
     * @param fieldValue   the value that was not found
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

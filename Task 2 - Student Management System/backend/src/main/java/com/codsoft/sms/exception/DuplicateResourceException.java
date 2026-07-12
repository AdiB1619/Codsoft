package com.codsoft.sms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an attempt is made to create or update a resource with a value that
 * violates a uniqueness constraint (e.g. duplicate email, roll number, or course code).
 *
 * <p>Maps to HTTP {@code 409 Conflict}.
 *
 * <p>Examples:
 * <ul>
 *   <li>Creating a student with an email that already belongs to another student</li>
 *   <li>Creating a student with a roll number that already belongs to another student</li>
 *   <li>Creating a course with a course code that already exists</li>
 * </ul>
 *
 * <p>The {@link com.codsoft.sms.exception.GlobalExceptionHandler} (Prompt 10)
 * intercepts this exception and returns a structured {@code ErrorResponse}.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a {@link DuplicateResourceException} with a descriptive message.
     *
     * @param resourceName the type of resource (e.g. {@code "Student"})
     * @param fieldName    the field that is duplicated (e.g. {@code "email"})
     * @param fieldValue   the duplicated value
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

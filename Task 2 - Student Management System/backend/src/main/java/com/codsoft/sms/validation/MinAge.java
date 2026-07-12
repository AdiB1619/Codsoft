package com.codsoft.sms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Bean Validation constraint that checks a {@link java.time.LocalDate} field
 * represents a date at least {@link #value()} years in the past — i.e., the person
 * is at least that many years old.
 *
 * <p>Used on {@code StudentRequestDTO.dateOfBirth} to enforce the rule:
 * "Student must be at least 10 years old" (SDD Section 10).
 *
 * <p>Null values are treated as valid by this constraint (null-checking is the
 * responsibility of {@code @NotNull} on the same field).
 *
 * <p>The constraint logic lives in {@link MinAgeValidator}.
 */
@Documented
@Constraint(validatedBy = MinAgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAge {

    /** The minimum age in years that the annotated date must satisfy. */
    int value() default 0;

    /** Validation failure message. */
    String message() default "Age requirement not met";

    /** Validation groups — leave empty for default group. */
    Class<?>[] groups() default {};

    /** Payload — for attaching metadata to a constraint. */
    Class<? extends Payload>[] payload() default {};
}

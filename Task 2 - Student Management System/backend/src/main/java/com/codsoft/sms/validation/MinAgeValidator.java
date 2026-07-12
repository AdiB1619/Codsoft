package com.codsoft.sms.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validator for the {@link MinAge} constraint.
 *
 * <p>Returns {@code true} (valid) when:
 * <ul>
 *   <li>the value is {@code null} (null-checking belongs to {@code @NotNull}), or</li>
 *   <li>the date is at least {@code minAge} years before today.</li>
 * </ul>
 */
public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    private int minAge;

    @Override
    public void initialize(MinAge annotation) {
        this.minAge = annotation.value();
    }

    /**
     * Checks that {@code dateOfBirth} is at least {@code minAge} years in the past.
     *
     * @param dateOfBirth the date to validate; {@code null} is considered valid
     * @param context     constraint validator context (unused)
     * @return {@code true} if the date satisfies the minimum-age requirement
     */
    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true; // null is handled by @NotNull
        }
        return dateOfBirth.plusYears(minAge).isBefore(LocalDate.now())
                || dateOfBirth.plusYears(minAge).isEqual(LocalDate.now());
    }
}

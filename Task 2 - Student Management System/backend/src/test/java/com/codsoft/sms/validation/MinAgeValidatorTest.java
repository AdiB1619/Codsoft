package com.codsoft.sms.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinAgeValidator Tests")
class MinAgeValidatorTest {

    private MinAgeValidator validator;

    @Mock
    private MinAge minAgeAnnotation;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new MinAgeValidator();
    }

    @Test
    @DisplayName("Validates when date of birth is null")
    void isValid_nullDate_returnsTrue() {
        when(minAgeAnnotation.value()).thenReturn(18);
        validator.initialize(minAgeAnnotation);

        boolean result = validator.isValid(null, context);
        
        assertThat(result).isTrue(); // Let @NotNull handle nulls
    }

    @Test
    @DisplayName("Validates when person is older than minimum age")
    void isValid_older_returnsTrue() {
        when(minAgeAnnotation.value()).thenReturn(18);
        validator.initialize(minAgeAnnotation);

        LocalDate dob = LocalDate.now().minusYears(20);
        boolean result = validator.isValid(dob, context);
        
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Validates when person is exactly the minimum age")
    void isValid_exactAge_returnsTrue() {
        when(minAgeAnnotation.value()).thenReturn(18);
        validator.initialize(minAgeAnnotation);

        LocalDate dob = LocalDate.now().minusYears(18);
        boolean result = validator.isValid(dob, context);
        
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Invalidates when person is younger than minimum age")
    void isValid_younger_returnsFalse() {
        when(minAgeAnnotation.value()).thenReturn(18);
        validator.initialize(minAgeAnnotation);

        LocalDate dob = LocalDate.now().minusYears(17).minusMonths(11);
        boolean result = validator.isValid(dob, context);
        
        assertThat(result).isFalse();
    }
}

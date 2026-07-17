package com.yourcompany.currencyconverter.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConversionRequest} validation constraints.
 *
 * <p>Uses the Jakarta Validator API directly — no Spring context is loaded.
 * This makes the tests extremely fast and purely focused on the DTO's
 * constraint annotations.
 *
 * <p>Test matrix:
 * <ul>
 *   <li>Valid request — no violations expected.</li>
 *   <li>Blank / null {@code from} field — violation expected.</li>
 *   <li>Blank / null {@code to} field — violation expected.</li>
 *   <li>Invalid currency code formats (lowercase, too long, digits) — violation expected.</li>
 *   <li>Null amount — violation expected.</li>
 *   <li>Zero amount — violation expected.</li>
 *   <li>Negative amount — violation expected.</li>
 *   <li>Amount below minimum (0.001) — violation expected.</li>
 *   <li>Amount at minimum boundary (0.01) — no violation expected.</li>
 *   <li>Multiple simultaneous violations — all reported at once.</li>
 * </ul>
 */
@DisplayName("ConversionRequest – Validation Constraint Tests")
class ConversionRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        // Build the Jakarta Validator once for the entire test class (expensive to create)
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // -------------------------------------------------------------------------
    // Happy-path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid request with correct codes and positive amount passes all constraints")
    void validRequest_shouldHaveNoViolations() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("100.00"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Amount at exact minimum boundary (0.01) is valid")
    void amount_atMinimumBoundary_shouldBeValid() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("0.01"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    // -------------------------------------------------------------------------
    // 'from' field validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Null 'from' field produces a NotBlank violation")
    void from_null_shouldViolateNotBlank() {
        ConversionRequest request = new ConversionRequest(null, "EUR", new BigDecimal("100"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("from"));
    }

    @Test
    @DisplayName("Blank 'from' field produces a NotBlank violation")
    void from_blank_shouldViolateNotBlank() {
        ConversionRequest request = new ConversionRequest("   ", "EUR", new BigDecimal("100"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("from"));
    }

    @ParameterizedTest(name = "from=''{0}'' should fail pattern validation")
    @ValueSource(strings = {"usd", "US", "USDD", "us1", "123", "U$D", "us "})
    @DisplayName("Invalid 'from' code formats all fail the ^[A-Z]{3}$ pattern")
    void from_invalidFormats_shouldViolatePattern(String invalidCode) {
        ConversionRequest request = new ConversionRequest(invalidCode, "EUR", new BigDecimal("100"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .as("Expected pattern violation for from='%s'", invalidCode)
                .anyMatch(v -> v.getPropertyPath().toString().equals("from"));
    }

    // -------------------------------------------------------------------------
    // 'to' field validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Null 'to' field produces a NotBlank violation")
    void to_null_shouldViolateNotBlank() {
        ConversionRequest request = new ConversionRequest("USD", null, new BigDecimal("100"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("to"));
    }

    @ParameterizedTest(name = "to=''{0}'' should fail pattern validation")
    @ValueSource(strings = {"eur", "EU", "EURR", "eu1", "€UR"})
    @DisplayName("Invalid 'to' code formats all fail the ^[A-Z]{3}$ pattern")
    void to_invalidFormats_shouldViolatePattern(String invalidCode) {
        ConversionRequest request = new ConversionRequest("USD", invalidCode, new BigDecimal("100"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .as("Expected pattern violation for to='%s'", invalidCode)
                .anyMatch(v -> v.getPropertyPath().toString().equals("to"));
    }

    // -------------------------------------------------------------------------
    // 'amount' field validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Null amount produces a NotNull violation")
    void amount_null_shouldViolateNotNull() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", null);

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    @DisplayName("Zero amount violates DecimalMin(0.01)")
    void amount_zero_shouldViolateDecimalMin() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", BigDecimal.ZERO);

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    @DisplayName("Negative amount violates DecimalMin(0.01)")
    void amount_negative_shouldViolateDecimalMin() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("-50.00"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    @DisplayName("Amount just below minimum (0.009) violates DecimalMin(0.01)")
    void amount_justBelowMinimum_shouldViolateDecimalMin() {
        ConversionRequest request = new ConversionRequest("USD", "EUR", new BigDecimal("0.009"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    // -------------------------------------------------------------------------
    // Multiple violations at once
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Completely invalid request reports violations on all three fields")
    void allFieldsInvalid_shouldReportThreeViolations() {
        // from=null, to="bad", amount=-1 — all three fields invalid
        ConversionRequest request = new ConversionRequest(null, "bad", new BigDecimal("-1"));

        Set<ConstraintViolation<ConversionRequest>> violations = validator.validate(request);

        // Should have at least one violation per field
        assertThat(violations).hasSizeGreaterThanOrEqualTo(3);

        Set<String> violatedFields = new java.util.HashSet<>();
        violations.forEach(v -> violatedFields.add(v.getPropertyPath().toString()));
        assertThat(violatedFields).contains("from", "to", "amount");
    }

    // -------------------------------------------------------------------------
    // ConversionResponse — construction and accessor tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ConversionResponse stores all fields correctly via constructor")
    void conversionResponse_constructorAndGetters_workCorrectly() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        ConversionResponse response = new ConversionResponse(
                "USD", "EUR",
                new BigDecimal("100.00"),
                new BigDecimal("85.42"),
                new BigDecimal("0.8542"),
                now);

        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getTo()).isEqualTo("EUR");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("85.42"));
        assertThat(response.getRate()).isEqualByComparingTo(new BigDecimal("0.8542"));
        assertThat(response.getTimestamp()).isEqualTo(now);
    }
}

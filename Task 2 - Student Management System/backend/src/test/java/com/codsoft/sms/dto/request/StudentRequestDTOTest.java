package com.codsoft.sms.dto.request;

import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests verifying Bean Validation constraints on {@link StudentRequestDTO}.
 *
 * <p>Validates that the rules defined in SDD Section 10 are correctly enforced.
 */
@DisplayName("StudentRequestDTO Validation Tests")
class StudentRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private StudentRequestDTO.StudentRequestDTOBuilder validDTOBuilder() {
        return StudentRequestDTO.builder()
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .phoneNumber("+919876543210")
                .dateOfBirth(LocalDate.now().minusYears(15))
                .gender(Gender.MALE)
                .address("221B Baker Street, Pune, MH")
                .rollNumber("CS2023045")
                .courseId(1L)
                .enrollmentDate(LocalDate.now().minusMonths(1))
                .grade(new BigDecimal("85.5"))
                .status(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Valid DTO passes all constraints")
    void validDTO_passesValidation() {
        StudentRequestDTO dto = validDTOBuilder().build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    // =========================================================================
    // firstName / lastName validation
    // =========================================================================

    @Test
    @DisplayName("firstName must not be blank")
    void firstName_blank_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().firstName("   ").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        // Expect @NotBlank, @Size(min=2), and @Pattern failure
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("First name is required"));
    }

    @Test
    @DisplayName("firstName rejects invalid characters")
    void firstName_invalidChars_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().firstName("Aarav123").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("First name must contain only letters, spaces, hyphens, or apostrophes");
    }

    @Test
    @DisplayName("lastName rejects short values")
    void lastName_tooShort_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().lastName("S").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        // Expect @Size and @Pattern failure
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Last name must be 2–50 characters"));
    }

    // =========================================================================
    // email validation
    // =========================================================================

    @Test
    @DisplayName("email must be valid format")
    void email_invalidFormat_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().email("not-an-email").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Enter a valid email address");
    }

    // =========================================================================
    // phoneNumber validation
    // =========================================================================

    @Test
    @DisplayName("phoneNumber rejects letters")
    void phoneNumber_letters_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().phoneNumber("+91ABCDEFGH").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Enter a valid phone number (10–13 digits, optional leading +)");
    }

    @Test
    @DisplayName("phoneNumber allows optional leading plus")
    void phoneNumber_withoutPlus_isValid() {
        StudentRequestDTO dto = validDTOBuilder().phoneNumber("9876543210").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    // =========================================================================
    // dateOfBirth validation (@MinAge)
    // =========================================================================

    @Test
    @DisplayName("dateOfBirth rejects age under 10")
    void dateOfBirth_under10_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().dateOfBirth(LocalDate.now().minusYears(9)).build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Student must be at least 10 years old");
    }

    // =========================================================================
    // rollNumber validation
    // =========================================================================

    @Test
    @DisplayName("rollNumber rejects invalid format")
    void rollNumber_invalidFormat_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().rollNumber("c2023").build(); // lowercase, too few digits
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Roll number format is invalid (e.g. CS2023045)");
    }

    @Test
    @DisplayName("rollNumber accepts long sequences up to 9 digits")
    void rollNumber_longSequence_isValid() {
        StudentRequestDTO dto = validDTOBuilder().rollNumber("MCA202312345").build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    // =========================================================================
    // enrollmentDate validation
    // =========================================================================

    @Test
    @DisplayName("enrollmentDate must not be in the future")
    void enrollmentDate_future_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().enrollmentDate(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Enrollment date cannot be in the future");
    }

    // =========================================================================
    // grade validation
    // =========================================================================

    @Test
    @DisplayName("grade rejects negative values")
    void grade_negative_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().grade(new BigDecimal("-1.0")).build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Grade must be 0 or above");
    }

    @Test
    @DisplayName("grade rejects values over 100")
    void grade_over100_isInvalid() {
        StudentRequestDTO dto = validDTOBuilder().grade(new BigDecimal("100.1")).build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Grade must be 100 or below");
    }

    @Test
    @DisplayName("grade allows null")
    void grade_null_isValid() {
        StudentRequestDTO dto = validDTOBuilder().grade(null).build();
        Set<ConstraintViolation<StudentRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}

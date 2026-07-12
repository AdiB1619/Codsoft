package com.codsoft.sms.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests verifying Bean Validation constraints on {@link CourseRequestDTO}.
 *
 * <p>Validates that the rules defined in SDD Section 10 are correctly enforced
 * before the request reaches the controller/service layer.
 */
@DisplayName("CourseRequestDTO Validation Tests")
class CourseRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private CourseRequestDTO.CourseRequestDTOBuilder validDTOBuilder() {
        return CourseRequestDTO.builder()
                .courseCode("CSE")
                .courseName("Computer Science and Engineering")
                .department("Engineering")
                .durationYears(4);
    }

    @Test
    @DisplayName("Valid DTO passes all constraints")
    void validDTO_passesValidation() {
        CourseRequestDTO dto = validDTOBuilder().build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    // =========================================================================
    // courseCode validation
    // =========================================================================

    @Test
    @DisplayName("courseCode must not be blank")
    void courseCode_blank_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().courseCode("   ").build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Course code is required");
    }

    @Test
    @DisplayName("courseCode must not exceed 15 characters")
    void courseCode_tooLong_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().courseCode("THIS-IS-WAY-TOO-LONG").build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Course code must be at most 15 characters");
    }

    // =========================================================================
    // courseName validation
    // =========================================================================

    @Test
    @DisplayName("courseName must not be blank")
    void courseName_blank_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().courseName("").build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Course name is required");
    }

    @Test
    @DisplayName("courseName must not exceed 100 characters")
    void courseName_tooLong_isInvalid() {
        String longName = "a".repeat(101);
        CourseRequestDTO dto = validDTOBuilder().courseName(longName).build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Course name must be at most 100 characters");
    }

    // =========================================================================
    // department validation
    // =========================================================================

    @Test
    @DisplayName("department must not be blank")
    void department_blank_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().department(null).build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Department is required");
    }

    @Test
    @DisplayName("department must not exceed 100 characters")
    void department_tooLong_isInvalid() {
        String longDept = "a".repeat(101);
        CourseRequestDTO dto = validDTOBuilder().department(longDept).build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Department must be at most 100 characters");
    }

    // =========================================================================
    // durationYears validation
    // =========================================================================

    @Test
    @DisplayName("durationYears must not be null")
    void durationYears_null_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().durationYears(null).build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Duration in years is required");
    }

    @Test
    @DisplayName("durationYears must be positive")
    void durationYears_zeroOrNegative_isInvalid() {
        CourseRequestDTO dto = validDTOBuilder().durationYears(0).build();
        Set<ConstraintViolation<CourseRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Duration in years must be a positive number");
    }
}

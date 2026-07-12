package com.codsoft.sms.dto.request;

import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.codsoft.sms.validation.MinAge;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating or fully updating a student.
 *
 * <p>Used by:
 * <ul>
 *   <li>{@code POST /api/v1/students} — create a new student</li>
 *   <li>{@code PUT  /api/v1/students/{id}} — full replacement update</li>
 * </ul>
 *
 * <p>Validation rules per SDD Section 10. Every constraint here has a
 * corresponding rule in the frontend {@code utils/validators.js}. Backend
 * validation is the authoritative source of truth — the frontend rules
 * exist for instant user feedback only.
 *
 * <p>Uniqueness of {@code email} and {@code rollNumber} is not enforced here —
 * it is checked in the Service layer before any DB write, returning
 * {@code 409 Conflict} when violated.
 *
 * <p>The {@code courseId} field references an existing course by its database ID.
 * Existence is verified in the Service layer ({@code 404 Not Found} on mismatch).
 */
@Getter
@Builder
public final class StudentRequestDTO {

    /**
     * Given name.
     * 2–50 characters; letters, spaces, hyphens, and apostrophes only.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2–50 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s'\\-]{2,50}$",
            message = "First name must contain only letters, spaces, hyphens, or apostrophes"
    )
    private final String firstName;

    /**
     * Family name.
     * 2–50 characters; letters, spaces, hyphens, and apostrophes only.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2–50 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s'\\-]{2,50}$",
            message = "Last name must contain only letters, spaces, hyphens, or apostrophes"
    )
    private final String lastName;

    /**
     * Email address — must be unique across all students.
     * RFC-5322-compatible format enforced by {@link Email}.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private final String email;

    /**
     * Contact phone number.
     * 10–13 digits with an optional leading {@code +}.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,13}$",
            message = "Enter a valid phone number (10–13 digits, optional leading +)"
    )
    private final String phoneNumber;

    /**
     * Date of birth. Must be in the past and the student must be at least 10 years old.
     * The {@link MinAge} constraint is a custom validator defined in the {@code validation}
     * package; {@code @Past} alone cannot express a minimum-age requirement.
     */
    @NotNull(message = "Date of birth is required")
    @MinAge(value = 10, message = "Student must be at least 10 years old")
    private final LocalDate dateOfBirth;

    /**
     * Biological gender — one of {@code MALE}, {@code FEMALE}, {@code OTHER}.
     * Deserialized from the JSON string representation by Jackson.
     */
    @NotNull(message = "Gender is required")
    private final Gender gender;

    /**
     * Residential or correspondence address.
     * 5–200 characters.
     */
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be 5–200 characters")
    private final String address;

    /**
     * Unique roll number assigned by the institution.
     * Format: 2–4 uppercase letters followed by 3–9 digits (e.g. {@code CS2023045}).
     * The numeric portion accommodates a 4-digit year plus up to a 5-digit sequence.
     */
    @NotBlank(message = "Roll number is required")
    @Pattern(
            regexp = "^[A-Z]{2,4}[0-9]{3,9}$",
            message = "Roll number format is invalid (e.g. CS2023045)"
    )
    private final String rollNumber;

    /**
     * Database ID of the course the student is enrolling in.
     * Existence is verified in the Service layer.
     */
    @NotNull(message = "Course is required")
    private final Long courseId;

    /**
     * Date of formal enrollment. Must not be in the future.
     */
    @NotNull(message = "Enrollment date is required")
    @PastOrPresent(message = "Enrollment date cannot be in the future")
    private final LocalDate enrollmentDate;

    /**
     * Academic grade as a percentage. Optional — {@code null} until first recorded.
     * When provided, must be between 0 and 100.
     */
    @DecimalMin(value = "0", message = "Grade must be 0 or above")
    @DecimalMax(value = "100", message = "Grade must be 100 or below")
    private final BigDecimal grade;

    /**
     * Enrollment lifecycle status. Defaults to {@code ACTIVE} when not supplied,
     * but is always included in the request body to make the intended state explicit.
     */
    @NotNull(message = "Status is required")
    private final StudentStatus status;
}

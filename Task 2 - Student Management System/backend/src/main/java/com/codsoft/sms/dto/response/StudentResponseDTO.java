package com.codsoft.sms.dto.response;

import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO representing a student's full profile as returned by the API.
 *
 * <p>This is the shape serialized into the {@code data} field of
 * {@link ApiResponse} for single-student endpoints (create, get-by-id, update).
 * For the student list, an array of these objects appears inside
 * {@link PagedResponse#getContent()}.
 *
 * <p>Matches the success-response shape shown in SDD Section 7.3:
 * <pre>{@code
 * {
 *   "id": 101,
 *   "firstName": "Aarav",
 *   "lastName":  "Sharma",
 *   "email":     "aarav.sharma@example.com",
 *   "rollNumber": "CS2023045",
 *   "course":    { "id": 3, "courseCode": "CSE", "courseName": "Computer Science" },
 *   "status":    "ACTIVE",
 *   "profileImageUrl": null,
 *   "createdAt": "2026-07-07T10:15:30"
 * }
 * }</pre>
 *
 * <p>The {@link com.codsoft.sms.entity.Student} entity must never be used
 * in place of this class in a controller or service return type.
 */
@Getter
@Builder
public final class StudentResponseDTO {

    /** Database primary key. */
    private final Long id;

    /** Unique roll number (e.g. {@code "CS2023045"}). */
    private final String rollNumber;

    /** Given name. */
    private final String firstName;

    /** Family name. */
    private final String lastName;

    /** Email address. */
    private final String email;

    /** Contact phone number. */
    private final String phoneNumber;

    /** Date of birth. */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate dateOfBirth;

    /** Biological gender enum value. */
    private final Gender gender;

    /** Residential or correspondence address. */
    private final String address;

    /**
     * The course the student is enrolled in — always a nested object, never a raw ID.
     * The frontend uses {@code course.id} as the FK reference when needed.
     */
    private final CourseResponseDTO course;

    /** Date of formal enrollment. */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate enrollmentDate;

    /**
     * Academic grade as a percentage (0–100). {@code null} until first recorded.
     * Jackson omits this field when null per global config.
     */
    private final BigDecimal grade;

    /** Current lifecycle status of the enrollment. */
    private final StudentStatus status;

    /**
     * Relative URL to the stored profile image.
     * {@code null} when no image has been uploaded.
     * Jackson omits this field when null per global config.
     */
    private final String profileImageUrl;

    /** Creation timestamp. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    /** Last-modified timestamp. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;
}

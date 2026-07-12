package com.codsoft.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Request DTO for creating a new course ({@code POST /api/v1/courses}).
 *
 * <p>Carries all fields required to persist a {@link com.codsoft.sms.entity.Course}
 * without exposing the entity class to the controller layer. Bean Validation
 * annotations here define the server-side validation contract; the frontend
 * validators in {@code utils/validators.js} mirror these rules for instant feedback.
 *
 * <p>Uniqueness of {@code courseCode} is not enforced here — it is checked in the
 * Service layer and returns {@code 409 Conflict} when violated.
 */
@Getter
@Builder
public final class CourseRequestDTO {

    /**
     * Short, unique identifier for the course (e.g. {@code "CSE"}, {@code "BBA"}).
     * Max 15 characters — matches the {@code course_code VARCHAR(15)} column.
     */
    @NotBlank(message = "Course code is required")
    @Size(max = 15, message = "Course code must be at most 15 characters")
    private final String courseCode;

    /**
     * Human-readable name of the course (e.g. "Computer Science and Engineering").
     * Max 100 characters — matches the {@code course_name VARCHAR(100)} column.
     */
    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name must be at most 100 characters")
    private final String courseName;

    /**
     * Academic department offering the course (e.g. "Engineering", "Management").
     * Max 100 characters — matches the {@code department VARCHAR(100)} column.
     */
    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must be at most 100 characters")
    private final String department;

    /**
     * Number of years the course runs (e.g. 4 for B.Tech, 2 for M.Tech).
     * Must be a positive integer.
     */
    @NotNull(message = "Duration in years is required")
    @Positive(message = "Duration in years must be a positive number")
    private final Integer durationYears;
}

package com.codsoft.sms.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO representing a course as it appears in API responses.
 *
 * <p>Used both as a standalone response (e.g. list-courses endpoint) and
 * as a nested field inside {@link StudentResponseDTO#getCourse()}.
 *
 * <p>This class exposes only the fields relevant to the API consumer —
 * the {@link com.codsoft.sms.entity.Course} entity itself never crosses
 * the controller boundary.
 */
@Getter
@Builder
public final class CourseResponseDTO {

    /** Database primary key. */
    private final Long id;

    /** Short code uniquely identifying the course (e.g. {@code "CSE"}). */
    private final String courseCode;

    /** Human-readable course name (e.g. {@code "Computer Science and Engineering"}). */
    private final String courseName;

    /** Academic department responsible for the course. */
    private final String department;

    /** Duration of the course in years. */
    private final Integer durationYears;
}

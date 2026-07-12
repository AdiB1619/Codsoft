package com.codsoft.sms.service;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;

import java.util.List;

/**
 * Business operations for {@link com.codsoft.sms.entity.Course}.
 *
 * <p>All method contracts:
 * <ul>
 *   <li>Accept and return DTOs only — entities never cross this boundary.</li>
 *   <li>Throw {@link com.codsoft.sms.exception.ResourceNotFoundException} (404)
 *       when a requested resource does not exist.</li>
 *   <li>Throw {@link com.codsoft.sms.exception.DuplicateResourceException} (409)
 *       when a uniqueness constraint would be violated.</li>
 * </ul>
 *
 * <p>Implementation: {@link com.codsoft.sms.service.impl.CourseServiceImpl}.
 */
public interface CourseService {

    /**
     * Returns all courses ordered by {@code course_name} ascending.
     * Primarily used to populate dropdowns and the course filter.
     *
     * @return an unmodifiable list of all courses as DTOs; empty list if none exist
     */
    List<CourseResponseDTO> getAllCourses();

    /**
     * Returns a single course by its database primary key.
     *
     * @param id the course ID
     * @return the matching course DTO
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if no course with {@code id} exists
     */
    CourseResponseDTO getCourseById(Long id);

    /**
     * Persists a new course.
     *
     * @param requestDTO the inbound course data
     * @return the persisted course DTO (including the generated {@code id})
     * @throws com.codsoft.sms.exception.DuplicateResourceException if {@code courseCode} already exists
     */
    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);
}

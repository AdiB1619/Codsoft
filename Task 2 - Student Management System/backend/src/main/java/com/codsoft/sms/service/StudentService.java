package com.codsoft.sms.service;

import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.PagedResponse;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.enums.StudentStatus;
import org.springframework.data.domain.Pageable;

/**
 * Business operations for {@link com.codsoft.sms.entity.Student}.
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
 * <p>Implementation: {@link com.codsoft.sms.service.impl.StudentServiceImpl}.
 */
public interface StudentService {

    /**
     * Returns a paginated, optionally filtered and searched list of students.
     *
     * <p>Filter criteria are optional — omit any to widen the result set:
     * <ul>
     *   <li>{@code search} — case-insensitive partial match against
     *       {@code firstName}, {@code lastName}, {@code email}, or {@code rollNumber}</li>
     *   <li>{@code courseId} — restrict to students enrolled in the given course</li>
     *   <li>{@code status} — restrict to students with the given enrollment status</li>
     * </ul>
     *
     * @param search   optional free-text search term; {@code null} to skip
     * @param courseId optional course filter; {@code null} to skip
     * @param status   optional status filter; {@code null} to skip
     * @param pageable pagination and sort instructions
     * @return a {@link PagedResponse} wrapping the matching student DTOs
     */
    PagedResponse<StudentResponseDTO> getStudents(
            String search, Long courseId, StudentStatus status, Pageable pageable);

    /**
     * Returns a single student by their database primary key.
     *
     * @param id the student ID
     * @return the matching student DTO
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if no student with {@code id} exists
     */
    StudentResponseDTO getStudentById(Long id);

    /**
     * Persists a new student.
     *
     * @param requestDTO the inbound student data
     * @return the persisted student DTO (including the generated {@code id} and audit timestamps)
     * @throws com.codsoft.sms.exception.DuplicateResourceException if {@code email} or
     *         {@code rollNumber} already belong to another student
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if {@code courseId}
     *         does not reference an existing course
     */
    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);

    /**
     * Fully replaces an existing student's data.
     *
     * @param id         the ID of the student to update
     * @param requestDTO the new student data
     * @return the updated student DTO
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if no student with {@code id}
     *         exists, or if {@code courseId} does not reference an existing course
     * @throws com.codsoft.sms.exception.DuplicateResourceException if {@code email} or
     *         {@code rollNumber} belongs to a <em>different</em> student
     */
    StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO);

    /**
     * Retrieves an unpaginated list of students matching the given filters (capped at 10,000 rows).
     *
     * @param search   optional free-text term
     * @param courseId optional course filter
     * @param status   optional status filter
     * @param sort     the sort configuration
     * @return a list of matching student summary DTOs
     */
    java.util.List<StudentResponseDTO> getStudentsForExport(String search, Long courseId, StudentStatus status, org.springframework.data.domain.Sort sort);

    /**
     * Updates only the student's status without affecting other fields.
     *
     * @param id     the primary key of the student
     * @param status the new status to apply
     * @return the updated student summary DTO
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if the student doesn't exist
     */
    StudentResponseDTO updateStudentStatus(Long id, StudentStatus status);

    /**
     * Uploads or replaces the student's profile image.
     *
     * @param id   the primary key of the student
     * @param file the multipart file (must be a valid image)
     * @return the updated student summary DTO containing the new image URL
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if the student doesn't exist
     * @throws com.codsoft.sms.exception.InvalidFileException if the file is invalid
     */
    StudentResponseDTO uploadProfileImage(Long id, org.springframework.web.multipart.MultipartFile file);

    /**
     * Removes the student's profile image and reverts the URL to null.
     *
     * @param id the primary key of the student
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if the student doesn't exist
     */
    void removeProfileImage(Long id);

    /**
     * Permanently deletes a student record from the database.
     * @param id the student ID
     * @throws com.codsoft.sms.exception.ResourceNotFoundException if no student with {@code id} exists
     */
    void deleteStudent(Long id);

    /**
     * Retrieves aggregated statistics for the dashboard.
     *
     * @return a {@link com.codsoft.sms.dto.response.DashboardStatsDTO} containing counts.
     */
    com.codsoft.sms.dto.response.DashboardStatsDTO getDashboardStats();
}

package com.codsoft.sms.repository;

import com.codsoft.sms.entity.Student;
import com.codsoft.sms.entity.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Student} entities.
 *
 * <p>Extends {@link JpaRepository} for standard CRUD operations and
 * {@link JpaSpecificationExecutor} to support the dynamic search, filter, and
 * sort composition built in the search/filter prompt (Prompt 12). Using the
 * {@code Specification} API keeps this interface lean — every combination of
 * search term, course filter, status filter, and sort direction is composed in
 * the Service layer without adding a new method here.
 *
 * <p><strong>Architecture note:</strong> This interface contains data-access
 * declarations only. All business rules, uniqueness enforcement, and query
 * composition live in the Service layer ({@code StudentService} /
 * {@code StudentServiceImpl}).
 */
public interface StudentRepository extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

    /**
     * Checks whether a student with the given email address already exists.
     *
     * <p>Used by the Service layer before INSERT to enforce email uniqueness,
     * producing a controlled {@code 409 Conflict} rather than a raw
     * {@code DataIntegrityViolationException}.
     *
     * @param email the email address to test
     * @return {@code true} if at least one student with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a student with the given roll number already exists.
     *
     * <p>Used by the Service layer before INSERT to enforce roll-number uniqueness.
     *
     * @param rollNumber the roll number to test (e.g. {@code "CS2023045"})
     * @return {@code true} if at least one student with this roll number exists
     */
    boolean existsByRollNumber(String rollNumber);

    /**
     * Checks whether a student with the given email exists, excluding the record
     * identified by {@code id}.
     *
     * <p>Used by the Service layer on UPDATE — prevents a false positive uniqueness
     * violation when a student's email is unchanged during an edit.
     *
     * @param email the email address to test
     * @param id    the ID of the student being updated (excluded from the check)
     * @return {@code true} if another student with this email exists
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Checks whether a student with the given roll number exists, excluding the
     * record identified by {@code id}.
     *
     * <p>Used by the Service layer on UPDATE — prevents a false positive uniqueness
     * violation when a student's roll number is unchanged during an edit.
     *
     * @param rollNumber the roll number to test
     * @param id         the ID of the student being updated (excluded from the check)
     * @return {@code true} if another student with this roll number exists
     */
    boolean existsByRollNumberAndIdNot(String rollNumber, Long id);

    /**
     * Finds a student by their unique email address.
     *
     * <p>Useful for lookups that start with an email rather than a numeric ID.
     *
     * @param email the email address to look up
     * @return an {@link Optional} containing the student, or empty if not found
     */
    Optional<Student> findByEmail(String email);

    /**
     * Checks whether any students are enrolled in the course identified by
     * {@code courseId}.
     *
     * <p>Used by the Service layer before deleting a course — if this returns
     * {@code true} the delete must be rejected (mirrors {@code ON DELETE RESTRICT}
     * at the application layer, providing a clear error message before hitting
     * the DB constraint).
     *
     * @param courseId the ID of the course to check
     * @return {@code true} if one or more students reference this course
     */
    boolean existsByCourseId(Long courseId);

    /**
     * Counts how many students currently have the given status.
     *
     * <p>Used by the Dashboard to build the summary stat cards without loading
     * full student records.
     *
     * @param status the status to count
     * @return the number of students in that status
     */
    long countByStatus(StudentStatus status);

    /**
     * Counts how many students enrolled on or after a specific date.
     *
     * <p>Used by the Dashboard to compute the "New This Month" metric.
     *
     * @param date the date to compare against (inclusive)
     * @return the number of students enrolled on or after this date
     */
    long countByEnrollmentDateGreaterThanEqual(java.time.LocalDate date);
}

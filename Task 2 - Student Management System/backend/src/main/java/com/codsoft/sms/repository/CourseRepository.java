package com.codsoft.sms.repository;

import com.codsoft.sms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Course} entities.
 *
 * <p>Extends {@link JpaRepository} for standard CRUD operations and
 * {@link JpaSpecificationExecutor} to support the dynamic query composition
 * added in the search/filter prompt (Prompt 12), avoiding an explosion of
 * rigid {@code findBy...} method combinations.
 *
 * <p><strong>Architecture note:</strong> This interface contains data-access
 * declarations only. All business rules and query composition logic live in the
 * Service layer ({@code CourseService} / {@code CourseServiceImpl}).
 */
public interface CourseRepository extends JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {

    /**
     * Checks whether a course with the given course code already exists.
     *
     * <p>Used by the Service layer to enforce uniqueness before attempting an
     * INSERT — prevents a {@code DataIntegrityViolationException} from bubbling
     * up unchecked and ensures a controlled {@code 409 Conflict} response.
     *
     * @param courseCode the course code to test (e.g. {@code "CSE"})
     * @return {@code true} if at least one course with this code exists
     */
    boolean existsByCourseCode(String courseCode);

    /**
     * Finds a course by its unique course code.
     *
     * <p>Used when resolving a course reference for display or validation
     * without loading by numeric ID.
     *
     * @param courseCode the course code to look up
     * @return an {@link Optional} containing the course, or empty if not found
     */
    Optional<Course> findByCourseCode(String courseCode);
}

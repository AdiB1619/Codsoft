package com.codsoft.sms.repository;

import com.codsoft.sms.entity.Course;
import com.codsoft.sms.entity.Student;
import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository-layer integration tests for {@link StudentRepository} and
 * {@link CourseRepository}.
 *
 * <p>Uses {@code @DataJpaTest} with {@code replace = NONE} so tests run against
 * the real MySQL instance and schema (identical to what the application uses),
 * rather than an embedded H2 database whose behaviour can diverge in subtle ways.
 *
 * <p>Each test method is wrapped in a transaction that rolls back on completion,
 * so the test data persisted in {@link #setUp()} never leaks into subsequent tests
 * or pollutes the development database. The seed data loaded by {@code seed-data.sql}
 * is preserved across all test runs.
 *
 * <p><strong>Pre-condition:</strong> The {@code sms_db} database must exist and have
 * the schema applied ({@code schema.sql}) before running these tests. Set
 * {@code DB_USERNAME} and {@code DB_PASSWORD} environment variables.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Student and Course Repository Tests")
class StudentCourseRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Course savedCourse;
    private Student savedStudent;

    /**
     * Inserts a known test course and student before each test.
     * The enclosing transaction rolls back after each test method, so these
     * records are automatically cleaned up — no manual delete is needed.
     */
    @BeforeEach
    void setUp() {
        savedCourse = courseRepository.save(
                Course.builder()
                        .courseCode("TEST01")
                        .courseName("Test Engineering")
                        .department("Test Department")
                        .durationYears(4)
                        .build()
        );

        savedStudent = studentRepository.save(
                Student.builder()
                        .rollNumber("TE2024999")
                        .firstName("Test")
                        .lastName("Student")
                        .email("test.student@example.com")
                        .phoneNumber("+919000000001")
                        .dateOfBirth(LocalDate.of(2000, 1, 1))
                        .gender(Gender.MALE)
                        .address("1 Test Street, Test City")
                        .course(savedCourse)
                        .enrollmentDate(LocalDate.of(2024, 7, 1))
                        .grade(null)
                        .status(StudentStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    // =========================================================================
    // CourseRepository Tests
    // =========================================================================

    @Test
    @DisplayName("existsByCourseCode returns true when course code exists")
    void existsByCourseCode_whenCodeExists_returnsTrue() {
        boolean exists = courseRepository.existsByCourseCode("TEST01");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCourseCode returns false when course code does not exist")
    void existsByCourseCode_whenCodeAbsent_returnsFalse() {
        boolean exists = courseRepository.existsByCourseCode("NONEXISTENT");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByCourseCode returns the course when it exists")
    void findByCourseCode_whenCodeExists_returnsCourse() {
        var result = courseRepository.findByCourseCode("TEST01");
        assertThat(result).isPresent();
        assertThat(result.get().getCourseName()).isEqualTo("Test Engineering");
    }

    @Test
    @DisplayName("findByCourseCode returns empty Optional when code does not exist")
    void findByCourseCode_whenCodeAbsent_returnsEmpty() {
        var result = courseRepository.findByCourseCode("NONEXISTENT");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Course is persisted and retrievable by ID")
    void save_course_persistsCorrectly() {
        var found = courseRepository.findById(savedCourse.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("TEST01");
        assertThat(found.get().getDurationYears()).isEqualTo(4);
    }

    // =========================================================================
    // StudentRepository — existsByEmail Tests
    // =========================================================================

    @Test
    @DisplayName("existsByEmail returns true when email exists")
    void existsByEmail_whenEmailExists_returnsTrue() {
        boolean exists = studentRepository.existsByEmail("test.student@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns false when email does not exist")
    void existsByEmail_whenEmailAbsent_returnsFalse() {
        boolean exists = studentRepository.existsByEmail("nobody@example.com");
        assertThat(exists).isFalse();
    }

    // =========================================================================
    // StudentRepository — existsByRollNumber Tests
    // =========================================================================

    @Test
    @DisplayName("existsByRollNumber returns true when roll number exists")
    void existsByRollNumber_whenRollNumberExists_returnsTrue() {
        boolean exists = studentRepository.existsByRollNumber("TE2024999");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByRollNumber returns false when roll number does not exist")
    void existsByRollNumber_whenRollNumberAbsent_returnsFalse() {
        boolean exists = studentRepository.existsByRollNumber("ZZ0000000");
        assertThat(exists).isFalse();
    }

    // =========================================================================
    // StudentRepository — existsByEmailAndIdNot Tests (UPDATE guard)
    // =========================================================================

    @Test
    @DisplayName("existsByEmailAndIdNot returns false for the same student's own email")
    void existsByEmailAndIdNot_ownEmail_returnsFalse() {
        // When updating a student, the student's own email must not count as a conflict.
        boolean conflict = studentRepository.existsByEmailAndIdNot(
                "test.student@example.com", savedStudent.getId());
        assertThat(conflict).isFalse();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot returns true when another student has that email")
    void existsByEmailAndIdNot_differentStudentHasEmail_returnsTrue() {
        // Save a second student with a different email.
        Student anotherStudent = studentRepository.save(
                Student.builder()
                        .rollNumber("TE2024998")
                        .firstName("Another")
                        .lastName("Student")
                        .email("another.student@example.com")
                        .phoneNumber("+919000000002")
                        .dateOfBirth(LocalDate.of(2001, 5, 15))
                        .gender(Gender.FEMALE)
                        .address("2 Other Street, Test City")
                        .course(savedCourse)
                        .enrollmentDate(LocalDate.of(2024, 7, 1))
                        .status(StudentStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        // Try to assign anotherStudent's email to savedStudent (ID excluded = savedStudent).
        boolean conflict = studentRepository.existsByEmailAndIdNot(
                "another.student@example.com", savedStudent.getId());
        assertThat(conflict).isTrue();
    }

    // =========================================================================
    // StudentRepository — existsByRollNumberAndIdNot Tests (UPDATE guard)
    // =========================================================================

    @Test
    @DisplayName("existsByRollNumberAndIdNot returns false for the same student's own roll number")
    void existsByRollNumberAndIdNot_ownRollNumber_returnsFalse() {
        boolean conflict = studentRepository.existsByRollNumberAndIdNot(
                "TE2024999", savedStudent.getId());
        assertThat(conflict).isFalse();
    }

    // =========================================================================
    // StudentRepository — existsByCourseId Tests
    // =========================================================================

    @Test
    @DisplayName("existsByCourseId returns true when students are enrolled in the course")
    void existsByCourseId_whenStudentsEnrolled_returnsTrue() {
        boolean hasStudents = studentRepository.existsByCourseId(savedCourse.getId());
        assertThat(hasStudents).isTrue();
    }

    @Test
    @DisplayName("existsByCourseId returns false for a course with no students")
    void existsByCourseId_whenNoStudents_returnsFalse() {
        Course emptyCourse = courseRepository.save(
                Course.builder()
                        .courseCode("EMPTY01")
                        .courseName("Empty Course")
                        .department("Test")
                        .durationYears(1)
                        .build()
        );
        boolean hasStudents = studentRepository.existsByCourseId(emptyCourse.getId());
        assertThat(hasStudents).isFalse();
    }

    // =========================================================================
    // StudentRepository — countByStatus Tests
    // =========================================================================

    @Test
    @DisplayName("countByStatus returns at least 1 for ACTIVE when setUp student is ACTIVE")
    void countByStatus_activeStudentPresent_returnsPositiveCount() {
        long count = studentRepository.countByStatus(StudentStatus.ACTIVE);
        // The setUp student is ACTIVE, so count must be >= 1.
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }

    // =========================================================================
    // StudentRepository — findByEmail Tests
    // =========================================================================

    @Test
    @DisplayName("findByEmail returns student when email matches")
    void findByEmail_whenEmailExists_returnsStudent() {
        var result = studentRepository.findByEmail("test.student@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getRollNumber()).isEqualTo("TE2024999");
    }

    @Test
    @DisplayName("findByEmail returns empty Optional when email does not exist")
    void findByEmail_whenEmailAbsent_returnsEmpty() {
        var result = studentRepository.findByEmail("nobody@example.com");
        assertThat(result).isEmpty();
    }

    // =========================================================================
    // StudentRepository — General persistence sanity check
    // =========================================================================

    @Test
    @DisplayName("Student default status is ACTIVE when not explicitly set")
    void save_student_defaultStatusIsActive() {
        Student studentWithDefaultStatus = studentRepository.save(
                Student.builder()
                        .rollNumber("TE2024997")
                        .firstName("Default")
                        .lastName("Status")
                        .email("default.status@example.com")
                        .phoneNumber("+919000000003")
                        .dateOfBirth(LocalDate.of(2002, 3, 20))
                        .gender(Gender.OTHER)
                        .address("3 Default Road, City")
                        .course(savedCourse)
                        .enrollmentDate(LocalDate.of(2024, 7, 1))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        assertThat(studentWithDefaultStatus.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Student ManyToOne relationship resolves to correct Course")
    void save_student_courseRelationshipResolvedCorrectly() {
        var found = studentRepository.findById(savedStudent.getId());
        assertThat(found).isPresent();
        // Trigger lazy load within the test transaction
        assertThat(found.get().getCourse().getCourseCode()).isEqualTo("TEST01");
    }
}

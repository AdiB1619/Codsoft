package com.codsoft.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistence mapping for the {@code courses} table.
 *
 * <p>A Course is the parent side of the one-to-many relationship with {@link Student}.
 * One course can have zero or more enrolled students; a student belongs to exactly one
 * course. The relationship is owned by {@link Student} via the {@code course_id} foreign key.
 *
 * <p>Course has no JPA auditing timestamps — the schema omits {@code created_at} /
 * {@code updated_at} for courses because courses are administrative master data whose
 * change history is not tracked in v1.0.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    /** Primary key — auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Short, unique identifier for the course (e.g. {@code CSE}, {@code BBA}).
     * Matches the {@code UNIQUE KEY idx_courses_course_code} constraint in schema.sql.
     */
    @Column(name = "course_code", nullable = false, unique = true, length = 15)
    private String courseCode;

    /** Human-readable name of the course (e.g. "Computer Science and Engineering"). */
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    /** Academic department offering this course (e.g. "Engineering", "Management"). */
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    /** Number of years the course runs (e.g. 4 for B.Tech, 2 for M.Tech). */
    @Column(name = "duration_years", nullable = false)
    private Integer durationYears;
}

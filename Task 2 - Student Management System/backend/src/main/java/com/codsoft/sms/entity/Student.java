package com.codsoft.sms.entity;

import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persistence mapping for the {@code students} table.
 *
 * <p>Student is the owning side of the many-to-one relationship with {@link Course}:
 * many students belong to one course, and the foreign key {@code course_id} lives in
 * this table. The relationship direction mirrors the schema's FK definition.
 *
 * <p>JPA auditing automatically populates {@code createdAt} on INSERT and
 * {@code updatedAt} on every UPDATE, provided {@link AuditingEntityListener} is
 * registered and {@code @EnableJpaAuditing} is active (see {@code JpaAuditingConfig}).
 *
 * <p><strong>Architecture note:</strong> This class must never be returned directly
 * from a controller. All API responses use dedicated Response DTOs (added in Prompt 6).
 */
@Entity
@Table(name = "students")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    /** Primary key — auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Unique roll number assigned by the institution (e.g. {@code CS2023045}).
     * Format: 2–4 uppercase letters followed by 3–6 digits (validated at the service layer).
     * Matches {@code UNIQUE KEY idx_students_roll_number} in schema.sql.
     */
    @Column(name = "roll_number", nullable = false, unique = true, length = 20)
    private String rollNumber;

    /** Given name of the student. */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /** Family name of the student. */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Primary contact email address, must be unique across all students.
     * Matches {@code UNIQUE KEY idx_students_email} in schema.sql.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /** Contact phone number (10–13 digits, optional leading {@code +}). */
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    /** Date of birth. Student must be at least 10 years old (validated at service layer). */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Biological gender, stored as a VARCHAR string ({@code MALE}/{@code FEMALE}/{@code OTHER}).
     * {@link EnumType#STRING} is mandatory — ordinal storage breaks on enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    /** Residential or correspondence address. */
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    /**
     * The course this student is enrolled in.
     * {@link FetchType#LAZY} avoids loading the full course record on every student fetch —
     * the course is joined explicitly when needed (e.g. in the mapper layer).
     * Student owns the FK ({@code course_id}); Course has no back-reference.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * Date on which the student formally enrolled in their course.
     * Must not be in the future (validated at service layer).
     */
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    /**
     * Academic grade as a percentage (0–100, two decimal places).
     * {@code null} until the student's first grade is recorded.
     */
    @Column(name = "grade", precision = 5, scale = 2)
    private BigDecimal grade;

    /**
     * Current lifecycle status of the student's enrollment.
     * Defaults to {@link StudentStatus#ACTIVE} on creation.
     * {@link EnumType#STRING} is mandatory — ordinal storage breaks on enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    /**
     * Relative URL path to the student's stored profile image (e.g.
     * {@code /uploads/students/uuid-avatar.jpg}). {@code null} when no image has been uploaded.
     */
    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    /**
     * Timestamp of the record's initial creation.
     * Set once by JPA auditing — never updated after the first INSERT.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the most recent update to this record.
     * Automatically refreshed by JPA auditing on every UPDATE.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

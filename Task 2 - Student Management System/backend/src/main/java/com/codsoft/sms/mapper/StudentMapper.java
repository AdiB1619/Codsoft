package com.codsoft.sms.mapper;

import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.entity.Student;

/**
 * Mapper between {@link Student} entities and their DTO representations.
 *
 * <p><strong>Mapping approach:</strong> Plain Java static methods — no MapStruct or
 * ModelMapper dependency. Every field mapping is explicit, readable, and trivially
 * debuggable without framework magic. See {@link CourseMapper} for a fuller rationale.
 *
 * <p><strong>toEntity contract:</strong> {@link #toEntity(StudentRequestDTO, Course)}
 * requires the resolved {@link Course} entity, not just a course ID. The Service layer
 * is responsible for loading the course from the repository (throwing
 * {@code ResourceNotFoundException} if absent) before calling this mapper — this keeps
 * the mapper stateless and free of repository dependencies.
 *
 * <p><strong>Null safety:</strong> Both mapping methods return {@code null} when the
 * primary input is {@code null}. The {@code course} parameter of
 * {@link #toEntity(StudentRequestDTO, Course)} must not be {@code null} when the DTO
 * is non-null (enforced by pre-condition in the Service layer).
 *
 * <p>This class is a utility with no mutable state — instantiation is intentionally
 * prevented.
 */
public final class StudentMapper {

    private StudentMapper() {
        // Utility class — do not instantiate.
    }

    /**
     * Converts a {@link Student} entity to a {@link StudentResponseDTO}.
     *
     * <p>The student's {@link Course} association is mapped inline via
     * {@link CourseMapper#toResponseDTO(Course)}, so this method must be called
     * inside a JPA transaction (or with an eagerly loaded course) to avoid a
     * {@code LazyInitializationException}.
     *
     * <p>Returns {@code null} if {@code student} is {@code null}.
     *
     * @param student the entity to map
     * @return the corresponding response DTO, or {@code null}
     */
    public static StudentResponseDTO toResponseDTO(Student student) {
        if (student == null) {
            return null;
        }
        return StudentResponseDTO.builder()
                .id(student.getId())
                .rollNumber(student.getRollNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .address(student.getAddress())
                .course(CourseMapper.toResponseDTO(student.getCourse()))
                .enrollmentDate(student.getEnrollmentDate())
                .grade(student.getGrade())
                .status(student.getStatus())
                .profileImageUrl(student.getProfileImageUrl())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    /**
     * Converts a {@link StudentRequestDTO} into a new (transient) {@link Student} entity.
     *
     * <p>The returned entity has no {@code id}, {@code createdAt}, or {@code updatedAt}
     * set — those are populated by the database and JPA auditing respectively when
     * {@code studentRepository.save()} is called.
     *
     * <p>Returns {@code null} if {@code dto} is {@code null}.
     *
     * @param dto    the inbound request DTO
     * @param course the resolved {@link Course} entity (must not be {@code null} when dto is non-null)
     * @return a new transient {@link Student} entity ready for persistence
     */
    public static Student toEntity(StudentRequestDTO dto, Course course) {
        if (dto == null) {
            return null;
        }
        return Student.builder()
                .rollNumber(dto.getRollNumber())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .address(dto.getAddress())
                .course(course)
                .enrollmentDate(dto.getEnrollmentDate())
                .grade(dto.getGrade())
                .status(dto.getStatus())
                .build();
    }

    /**
     * Applies the fields from a {@link StudentRequestDTO} onto an existing
     * {@link Student} entity for a full update ({@code PUT /api/v1/students/{id}}).
     *
     * <p>The entity's {@code id}, {@code createdAt} are left untouched.
     * {@code updatedAt} is refreshed automatically by JPA auditing on the next save.
     *
     * <p>This method mutates {@code existing} in place and returns it for fluent chaining.
     *
     * @param dto      the inbound update request
     * @param existing the managed entity to update
     * @param course   the (possibly new) resolved {@link Course} entity
     * @return the updated {@link Student} entity (same reference as {@code existing})
     */
    public static Student updateEntity(StudentRequestDTO dto, Student existing, Course course) {
        existing.setRollNumber(dto.getRollNumber());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setGender(dto.getGender());
        existing.setAddress(dto.getAddress());
        existing.setCourse(course);
        existing.setEnrollmentDate(dto.getEnrollmentDate());
        existing.setGrade(dto.getGrade());
        existing.setStatus(dto.getStatus());
        return existing;
    }
}

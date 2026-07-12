package com.codsoft.sms.mapper;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.entity.Course;

/**
 * Mapper between {@link Course} entities and their DTO representations.
 *
 * <p><strong>Mapping approach:</strong> Plain Java static methods — no MapStruct or
 * ModelMapper dependency. This choice keeps the {@code pom.xml} lean (no additional
 * dependency or annotation processor needed), makes every mapping step explicit and
 * easy to debug, and avoids any framework-specific annotation processing subtleties
 * with Lombok-generated builders. The tradeoff is a small amount of boilerplate that
 * is easy to extend as fields are added.
 *
 * <p><strong>Null safety:</strong> Both methods return {@code null} when the input
 * is {@code null}. Callers in the Service layer are responsible for validating that
 * the entity exists before calling the mapper (throwing
 * {@code ResourceNotFoundException} if not).
 *
 * <p>This class is a utility with no mutable state — instantiation is intentionally
 * prevented.
 */
public final class CourseMapper {

    private CourseMapper() {
        // Utility class — do not instantiate.
    }

    /**
     * Converts a {@link Course} entity to a {@link CourseResponseDTO}.
     *
     * <p>Returns {@code null} if {@code course} is {@code null}.
     *
     * @param course the entity to map
     * @return the corresponding response DTO, or {@code null}
     */
    public static CourseResponseDTO toResponseDTO(Course course) {
        if (course == null) {
            return null;
        }
        return CourseResponseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .department(course.getDepartment())
                .durationYears(course.getDurationYears())
                .build();
    }

    /**
     * Converts a {@link CourseRequestDTO} to a new (transient) {@link Course} entity.
     *
     * <p>The returned entity has no {@code id} set — it is a new, unpersisted object
     * ready to be passed to {@code courseRepository.save()}. Returns {@code null} if
     * {@code dto} is {@code null}.
     *
     * @param dto the inbound request DTO
     * @return a new transient {@link Course} entity, or {@code null}
     */
    public static Course toEntity(CourseRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Course.builder()
                .courseCode(dto.getCourseCode())
                .courseName(dto.getCourseName())
                .department(dto.getDepartment())
                .durationYears(dto.getDurationYears())
                .build();
    }
}

package com.codsoft.sms.mapper;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.entity.Course;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CourseMapper}.
 *
 * <p>Pure unit tests — no Spring context, no database. All inputs are
 * constructed directly and assertions use AssertJ.
 */
@DisplayName("CourseMapper Unit Tests")
class CourseMapperTest {

    // =========================================================================
    // toResponseDTO
    // =========================================================================

    @Test
    @DisplayName("toResponseDTO maps all fields correctly from a Course entity")
    void toResponseDTO_mapsAllFields() {
        Course course = Course.builder()
                .id(1L)
                .courseCode("CSE")
                .courseName("Computer Science and Engineering")
                .department("Engineering")
                .durationYears(4)
                .build();

        CourseResponseDTO dto = CourseMapper.toResponseDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCourseCode()).isEqualTo("CSE");
        assertThat(dto.getCourseName()).isEqualTo("Computer Science and Engineering");
        assertThat(dto.getDepartment()).isEqualTo("Engineering");
        assertThat(dto.getDurationYears()).isEqualTo(4);
    }

    @Test
    @DisplayName("toResponseDTO returns null when entity is null")
    void toResponseDTO_nullEntity_returnsNull() {
        assertThat(CourseMapper.toResponseDTO(null)).isNull();
    }

    // =========================================================================
    // toEntity
    // =========================================================================

    @Test
    @DisplayName("toEntity maps all fields correctly from a CourseRequestDTO")
    void toEntity_mapsAllFields() {
        CourseRequestDTO dto = CourseRequestDTO.builder()
                .courseCode("BBA")
                .courseName("Bachelor of Business Administration")
                .department("Management")
                .durationYears(3)
                .build();

        Course entity = CourseMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // new entity — no ID yet
        assertThat(entity.getCourseCode()).isEqualTo("BBA");
        assertThat(entity.getCourseName()).isEqualTo("Bachelor of Business Administration");
        assertThat(entity.getDepartment()).isEqualTo("Management");
        assertThat(entity.getDurationYears()).isEqualTo(3);
    }

    @Test
    @DisplayName("toEntity returns null when DTO is null")
    void toEntity_nullDto_returnsNull() {
        assertThat(CourseMapper.toEntity(null)).isNull();
    }

    // =========================================================================
    // Round-trip: entity → DTO → entity (field preservation)
    // =========================================================================

    @Test
    @DisplayName("Round-trip entity→DTO→entity preserves all fields")
    void roundTrip_entityToDtoToEntity_preservesFields() {
        Course original = Course.builder()
                .id(5L)
                .courseCode("MCA")
                .courseName("Master of Computer Applications")
                .department("Computer Apps")
                .durationYears(2)
                .build();

        CourseResponseDTO responseDTO = CourseMapper.toResponseDTO(original);

        // Simulate: create a new entity from equivalent request data
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .courseCode(responseDTO.getCourseCode())
                .courseName(responseDTO.getCourseName())
                .department(responseDTO.getDepartment())
                .durationYears(responseDTO.getDurationYears())
                .build();

        Course rebuilt = CourseMapper.toEntity(requestDTO);

        assertThat(rebuilt.getCourseCode()).isEqualTo(original.getCourseCode());
        assertThat(rebuilt.getCourseName()).isEqualTo(original.getCourseName());
        assertThat(rebuilt.getDepartment()).isEqualTo(original.getDepartment());
        assertThat(rebuilt.getDurationYears()).isEqualTo(original.getDurationYears());
        // ID is intentionally absent on the rebuilt entity (would be set by DB on save)
        assertThat(rebuilt.getId()).isNull();
    }
}

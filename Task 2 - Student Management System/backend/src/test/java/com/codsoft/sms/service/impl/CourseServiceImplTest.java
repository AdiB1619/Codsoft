package com.codsoft.sms.service.impl;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.ResourceNotFoundException;
import com.codsoft.sms.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CourseServiceImpl}.
 *
 * <p>Uses Mockito to isolate the service from the repository layer.
 * No Spring context is loaded — these are pure JVM unit tests.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseServiceImpl Unit Tests")
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    // =========================================================================
    // Fixture helpers
    // =========================================================================

    private Course buildCourse(Long id, String code) {
        return Course.builder()
                .id(id)
                .courseCode(code)
                .courseName("Computer Science and Engineering")
                .department("Engineering")
                .durationYears(4)
                .build();
    }

    private CourseRequestDTO buildRequestDTO(String code) {
        return CourseRequestDTO.builder()
                .courseCode(code)
                .courseName("Computer Science and Engineering")
                .department("Engineering")
                .durationYears(4)
                .build();
    }

    // =========================================================================
    // getAllCourses
    // =========================================================================

    @Test
    @DisplayName("getAllCourses returns mapped DTOs for all courses")
    void getAllCourses_returnsMappedDTOs() {
        given(courseRepository.findAll())
                .willReturn(List.of(buildCourse(1L, "CSE"), buildCourse(2L, "ECE")));

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCourseCode()).isEqualTo("CSE");
        assertThat(result.get(1).getCourseCode()).isEqualTo("ECE");
    }

    @Test
    @DisplayName("getAllCourses returns empty list when no courses exist")
    void getAllCourses_noCourses_returnsEmptyList() {
        given(courseRepository.findAll()).willReturn(List.of());
        assertThat(courseService.getAllCourses()).isEmpty();
    }

    // =========================================================================
    // getCourseById
    // =========================================================================

    @Test
    @DisplayName("getCourseById returns the correct DTO when course exists")
    void getCourseById_courseExists_returnsDTO() {
        given(courseRepository.findById(1L)).willReturn(Optional.of(buildCourse(1L, "CSE")));

        CourseResponseDTO result = courseService.getCourseById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCourseCode()).isEqualTo("CSE");
    }

    @Test
    @DisplayName("getCourseById throws ResourceNotFoundException when course does not exist")
    void getCourseById_courseAbsent_throwsNotFoundException() {
        given(courseRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course")
                .hasMessageContaining("99");
    }

    // =========================================================================
    // createCourse — happy path
    // =========================================================================

    @Test
    @DisplayName("createCourse persists and returns the new course DTO")
    void createCourse_validRequest_returnsSavedDTO() {
        CourseRequestDTO dto = buildRequestDTO("BCA");
        Course saved = buildCourse(3L, "BCA");

        given(courseRepository.existsByCourseCode("BCA")).willReturn(false);
        given(courseRepository.save(any(Course.class))).willReturn(saved);

        CourseResponseDTO result = courseService.createCourse(dto);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getCourseCode()).isEqualTo("BCA");
        verify(courseRepository).save(any(Course.class));
    }

    // =========================================================================
    // createCourse — duplicate courseCode
    // =========================================================================

    @Test
    @DisplayName("createCourse throws DuplicateResourceException when courseCode already exists")
    void createCourse_duplicateCourseCode_throwsDuplicateException() {
        CourseRequestDTO dto = buildRequestDTO("CSE");
        given(courseRepository.existsByCourseCode("CSE")).willReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Course")
                .hasMessageContaining("CSE");

        verify(courseRepository, never()).save(any());
    }
}

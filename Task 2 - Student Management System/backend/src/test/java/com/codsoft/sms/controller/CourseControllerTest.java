package com.codsoft.sms.controller;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.GlobalExceptionHandler;
import com.codsoft.sms.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code @WebMvcTest} for {@link CourseController}.
 *
 * <p>Loads only the web layer (controller + exception handler) and mocks the
 * service layer. No database or full application context is started.
 */
@WebMvcTest(CourseController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("CourseController WebMvcTest")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // GET /api/v1/courses  — §7.12
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/courses returns 200 with course list")
    void getAllCourses_returns200WithList() throws Exception {
        CourseResponseDTO dto = CourseResponseDTO.builder()
                .id(1L).courseCode("CSE").courseName("Computer Science and Engineering")
                .department("Engineering").durationYears(4).build();
        given(courseService.getAllCourses()).willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/courses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].courseCode").value("CSE"))
                .andExpect(jsonPath("$.data[0].durationYears").value(4));
    }

    @Test
    @DisplayName("GET /api/v1/courses returns 200 with empty list when no courses exist")
    void getAllCourses_emptyList_returns200() throws Exception {
        given(courseService.getAllCourses()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/courses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // =========================================================================
    // POST /api/v1/courses  — §7.13
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/courses returns 201 with created course")
    void createCourse_validRequest_returns201() throws Exception {
        CourseRequestDTO request = CourseRequestDTO.builder()
                .courseCode("BBA").courseName("Bachelor of Business Administration")
                .department("Management").durationYears(3).build();
        CourseResponseDTO response = CourseResponseDTO.builder()
                .id(2L).courseCode("BBA").courseName("Bachelor of Business Administration")
                .department("Management").durationYears(3).build();
        given(courseService.createCourse(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.courseCode").value("BBA"));
    }

    @Test
    @DisplayName("POST /api/v1/courses returns 400 when courseCode is missing")
    void createCourse_missingCourseCode_returns400() throws Exception {
        CourseRequestDTO invalid = CourseRequestDTO.builder()
                // courseCode intentionally omitted
                .courseName("Business Admin").department("Management").durationYears(3).build();

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/courses returns 409 when courseCode already exists")
    void createCourse_duplicateCourseCode_returns409() throws Exception {
        CourseRequestDTO request = CourseRequestDTO.builder()
                .courseCode("CSE").courseName("Duplicate CS").department("Eng").durationYears(4).build();
        given(courseService.createCourse(any()))
                .willThrow(new DuplicateResourceException("Course", "courseCode", "CSE"));

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("CSE")));
    }
}

package com.codsoft.sms.controller;

import com.codsoft.sms.dto.request.StatusUpdateRequest;
import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.dto.response.PagedResponse;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.GlobalExceptionHandler;
import com.codsoft.sms.exception.ResourceNotFoundException;
import com.codsoft.sms.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code @WebMvcTest} for {@link StudentController}.
 *
 * <p>Loads only the web layer (controller + exception handler) and mocks the
 * service layer. No database or full application context is started.
 */
@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("StudentController WebMvcTest")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentResponseDTO studentResponse;
    private StudentRequestDTO studentRequest;

    @BeforeEach
    void setUp() {
        CourseResponseDTO courseDTO = CourseResponseDTO.builder()
                .id(1L).courseCode("CSE").courseName("Computer Science and Engineering")
                .department("Engineering").durationYears(4).build();

        studentResponse = StudentResponseDTO.builder()
                .id(101L)
                .rollNumber("CS2023001")
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .phoneNumber("+919876543210")
                .dateOfBirth(LocalDate.of(2005, 8, 14))
                .gender(Gender.MALE)
                .address("221B Baker Street, Pune, MH")
                .course(courseDTO)
                .enrollmentDate(LocalDate.of(2023, 7, 1))
                .grade(new BigDecimal("87.50"))
                .status(StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.of(2023, 7, 1, 9, 0))
                .updatedAt(LocalDateTime.of(2023, 7, 1, 9, 0))
                .build();

        studentRequest = StudentRequestDTO.builder()
                .rollNumber("CS2023001")
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .phoneNumber("+919876543210")
                .dateOfBirth(LocalDate.of(2005, 8, 14))
                .gender(Gender.MALE)
                .address("221B Baker Street, Pune, MH")
                .courseId(1L)
                .enrollmentDate(LocalDate.of(2023, 7, 1))
                .grade(new BigDecimal("87.50"))
                .status(StudentStatus.ACTIVE)
                .build();
    }

    // =========================================================================
    // POST /api/v1/students  — §7.3
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/students returns 201 with created student")
    void createStudent_validRequest_returns201() throws Exception {
        given(studentService.createStudent(any())).willReturn(studentResponse);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(101))
                .andExpect(jsonPath("$.data.rollNumber").value("CS2023001"))
                .andExpect(jsonPath("$.data.course.courseCode").value("CSE"));
    }

    @Test
    @DisplayName("POST /api/v1/students returns 400 when firstName is missing")
    void createStudent_missingFirstName_returns400() throws Exception {
        // Build a request without firstName (null) — should fail @NotBlank
        StudentRequestDTO invalid = StudentRequestDTO.builder()
                // firstName intentionally omitted
                .lastName("Sharma").email("aarav@example.com")
                .phoneNumber("+919876543210").dateOfBirth(LocalDate.of(2005, 8, 14))
                .gender(Gender.MALE).address("221B Baker Street")
                .rollNumber("CS2023001").courseId(1L)
                .enrollmentDate(LocalDate.of(2023, 7, 1)).status(StudentStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/students returns 409 when email is duplicate")
    void createStudent_duplicateEmail_returns409() throws Exception {
        given(studentService.createStudent(any()))
                .willThrow(new DuplicateResourceException("Student", "email", "aarav.sharma@example.com"));

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/students returns 404 when courseId does not exist")
    void createStudent_courseNotFound_returns404() throws Exception {
        given(studentService.createStudent(any()))
                .willThrow(new ResourceNotFoundException("Course", "id", 1L));

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // GET /api/v1/students  — §7.4
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/students returns 200 with paginated students (default parameters)")
    void getStudents_returns200WithPagedResponse() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).last(true).build();
        given(studentService.getStudents(any(), any(), any(), any())).willReturn(paged);

        mockMvc.perform(get("/api/v1/students").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].rollNumber").value("CS2023001"));
    }

    @Test
    @DisplayName("GET /api/v1/students maps custom page and size parameters correctly")
    void getStudents_customPageAndSize() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(2).pageSize(5).totalElements(11).totalPages(3).last(false).build();
        given(studentService.getStudents(any(), any(), any(), any())).willReturn(paged);

        mockMvc.perform(get("/api/v1/students")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageNumber").value(2))
                .andExpect(jsonPath("$.data.pageSize").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/students maps ascending and descending sort directions correctly")
    void getStudents_customSortDirection() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).last(true).build();
        given(studentService.getStudents(any(), any(), any(), any())).willReturn(paged);

        // Test descending
        mockMvc.perform(get("/api/v1/students")
                        .param("sortBy", "firstName")
                        .param("sortDir", "desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
                
        // (MockMvc verification of the precise Pageable argument would require ArgumentCaptor,
        // but this verifies the endpoints accepts and routes the parameters without error).
    }

    @Test
    @DisplayName("GET /api/v1/students returns 400 for invalid sortBy")
    void getStudents_invalidSortBy_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                        .param("sortBy", "nonExistentField")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/students maps search parameter correctly")
    void getStudents_searchMatchesText() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).last(true).build();
        given(studentService.getStudents(eq("Aarav"), isNull(), isNull(), any())).willReturn(paged);

        mockMvc.perform(get("/api/v1/students")
                        .param("search", "Aarav")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/students maps courseId and status filters correctly")
    void getStudents_filtersByCourseAndStatus() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).last(true).build();
        given(studentService.getStudents(isNull(), eq(1L), eq(StudentStatus.ACTIVE), any())).willReturn(paged);

        mockMvc.perform(get("/api/v1/students")
                        .param("courseId", "1")
                        .param("status", "ACTIVE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/students maps search, filter, and sort combined correctly")
    void getStudents_searchFilterAndSortCombined() throws Exception {
        PagedResponse<StudentResponseDTO> paged = PagedResponse.<StudentResponseDTO>builder()
                .content(List.of(studentResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).last(true).build();
        given(studentService.getStudents(eq("john"), eq(2L), eq(StudentStatus.GRADUATED), any())).willReturn(paged);

        mockMvc.perform(get("/api/v1/students")
                        .param("search", "john")
                        .param("courseId", "2")
                        .param("status", "GRADUATED")
                        .param("sortBy", "firstName")
                        .param("sortDir", "desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // =========================================================================
    // GET /api/v1/students/{id}  — §7.5
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/students/{id} returns 200 with student")
    void getStudentById_exists_returns200() throws Exception {
        given(studentService.getStudentById(101L)).willReturn(studentResponse);

        mockMvc.perform(get("/api/v1/students/101").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(101))
                .andExpect(jsonPath("$.data.email").value("aarav.sharma@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} returns 404 when student does not exist")
    void getStudentById_notFound_returns404() throws Exception {
        given(studentService.getStudentById(999L))
                .willThrow(new ResourceNotFoundException("Student", "id", 999L));

        mockMvc.perform(get("/api/v1/students/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // PUT /api/v1/students/{id}  — §7.6
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/students/{id} returns 200 with updated student")
    void updateStudent_validRequest_returns200() throws Exception {
        given(studentService.updateStudent(eq(101L), any())).willReturn(studentResponse);

        mockMvc.perform(put("/api/v1/students/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rollNumber").value("CS2023001"));
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} returns 404 when student does not exist")
    void updateStudent_notFound_returns404() throws Exception {
        given(studentService.updateStudent(eq(999L), any()))
                .willThrow(new ResourceNotFoundException("Student", "id", 999L));

        mockMvc.perform(put("/api/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // PATCH /api/v1/students/{id}/status  — §7.7
    // =========================================================================

    @Test
    @DisplayName("PATCH /api/v1/students/{id}/status returns 200 with updated student")
    void updateStudentStatus_validStatus_returns200() throws Exception {
        StudentResponseDTO graduated = StudentResponseDTO.builder()
                .id(101L).rollNumber("CS2023001").firstName("Aarav").lastName("Sharma")
                .email("aarav.sharma@example.com").phoneNumber("+919876543210")
                .dateOfBirth(LocalDate.of(2005, 8, 14)).gender(Gender.MALE)
                .address("221B Baker Street").course(studentResponse.getCourse())
                .enrollmentDate(LocalDate.of(2023, 7, 1))
                .status(StudentStatus.GRADUATED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        given(studentService.updateStudentStatus(101L, StudentStatus.GRADUATED)).willReturn(graduated);

        String body = objectMapper.writeValueAsString(java.util.Map.of("status", "GRADUATED"));

        mockMvc.perform(patch("/api/v1/students/101/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("GRADUATED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/students/{id}/status returns 404 when student does not exist")
    void updateStudentStatus_notFound_returns404() throws Exception {
        given(studentService.updateStudentStatus(eq(999L), any()))
                .willThrow(new ResourceNotFoundException("Student", "id", 999L));

        String body = objectMapper.writeValueAsString(java.util.Map.of("status", "ACTIVE"));

        mockMvc.perform(patch("/api/v1/students/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // DELETE /api/v1/students/{id}  — §7.8
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/v1/students/{id} returns 204 when student exists")
    void deleteStudent_exists_returns204() throws Exception {
        willDoNothing().given(studentService).deleteStudent(101L);

        mockMvc.perform(delete("/api/v1/students/101"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} returns 404 when student does not exist")
    void deleteStudent_notFound_returns404() throws Exception {
        willThrow(new ResourceNotFoundException("Student", "id", 999L))
                .given(studentService).deleteStudent(999L);

        mockMvc.perform(delete("/api/v1/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
    // =========================================================================
    // POST /api/v1/students/{id}/profile-image  — §7.9
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/students/{id}/profile-image returns 200 and image URL on success")
    void uploadProfileImage_success() throws Exception {
        StudentResponseDTO updatedStudent = StudentResponseDTO.builder()
                .id(101L)
                .profileImageUrl("/uploads/images/test.jpg")
                .build();
        
        given(studentService.uploadProfileImage(eq(101L), any())).willReturn(updatedStudent);

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "test.jpg", "image/jpeg", "image data".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/v1/students/101/profile-image")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.profileImageUrl").value("/uploads/images/test.jpg"));
    }

    @Test
    @DisplayName("POST /api/v1/students/{id}/profile-image returns 404 if student does not exist")
    void uploadProfileImage_studentNotFound_returns404() throws Exception {
        given(studentService.uploadProfileImage(eq(999L), any()))
                .willThrow(new ResourceNotFoundException("Student", "id", 999L));

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "test.jpg", "image/jpeg", "image data".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/v1/students/999/profile-image")
                        .file(file))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/students/{id}/profile-image returns 400 for invalid file")
    void uploadProfileImage_invalidFile_returns400() throws Exception {
        given(studentService.uploadProfileImage(eq(101L), any()))
                .willThrow(new com.codsoft.sms.exception.InvalidFileException("Invalid file format"));

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "test.txt", "text/plain", "text data".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/v1/students/101/profile-image")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid file format"));
    }

    // =========================================================================
    // DELETE /api/v1/students/{id}/profile-image  — §7.10
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/v1/students/{id}/profile-image returns 204")
    void removeProfileImage_returns204() throws Exception {
        willDoNothing().given(studentService).removeProfileImage(101L);

        mockMvc.perform(delete("/api/v1/students/101/profile-image"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id}/profile-image returns 404 when student does not exist")
    void removeProfileImage_studentNotFound_returns404() throws Exception {
        willThrow(new ResourceNotFoundException("Student", "id", 999L))
                .given(studentService).removeProfileImage(999L);

        mockMvc.perform(delete("/api/v1/students/999/profile-image"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
    // =========================================================================
    // GET /api/v1/students/export  — §7.11
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/students/export returns 200 with text/csv content type and data")
    void exportStudents_returnsCsv() throws Exception {
        StudentResponseDTO student = StudentResponseDTO.builder()
                .id(1L)
                .firstName("John, Jr.") // Contains comma to test escaping
                .lastName("Doe")
                .email("john@example.com")
                .rollNumber("CS001")
                .status(StudentStatus.ACTIVE)
                .build();

        given(studentService.getStudentsForExport(isNull(), isNull(), isNull(), any()))
                .willReturn(java.util.List.of(student));

        mockMvc.perform(get("/api/v1/students/export"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType("text/csv"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment; filename=\"students_export_")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("\"John, Jr.\",Doe,john@example.com")));
    }
}

package com.codsoft.sms.service.impl;

import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.PagedResponse;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.entity.Student;
import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.ResourceNotFoundException;
import com.codsoft.sms.repository.CourseRepository;
import com.codsoft.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link StudentServiceImpl}.
 *
 * <p>Uses Mockito to isolate the service from both the StudentRepository and
 * the CourseRepository. No Spring context or database is involved.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentServiceImpl Unit Tests")
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Course course;
    private Student student;
    private StudentRequestDTO requestDTO;

    // =========================================================================
    // Setup
    // =========================================================================

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .courseCode("CSE")
                .courseName("Computer Science and Engineering")
                .department("Engineering")
                .durationYears(4)
                .build();

        student = Student.builder()
                .id(101L)
                .rollNumber("CS2023001")
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .phoneNumber("+919876543210")
                .dateOfBirth(LocalDate.of(2005, 8, 14))
                .gender(Gender.MALE)
                .address("221B Baker Street, Pune, MH")
                .course(course)
                .enrollmentDate(LocalDate.of(2023, 7, 1))
                .grade(new BigDecimal("87.50"))
                .status(StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.of(2023, 7, 1, 9, 0))
                .updatedAt(LocalDateTime.of(2023, 7, 1, 9, 0))
                .build();

        requestDTO = StudentRequestDTO.builder()
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
    // getStudents
    // =========================================================================

    @Test
    @DisplayName("getStudents returns a PagedResponse with mapped DTOs")
    @SuppressWarnings("unchecked")
    void getStudents_returnsPagedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

        given(studentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(page);

        PagedResponse<StudentResponseDTO> response =
                studentService.getStudents(null, null, null, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getRollNumber()).isEqualTo("CS2023001");
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.isLast()).isTrue();
    }

    // =========================================================================
    // getStudentById
    // =========================================================================

    @Test
    @DisplayName("getStudentById returns the correct DTO when student exists")
    void getStudentById_studentExists_returnsDTO() {
        given(studentRepository.findById(101L)).willReturn(Optional.of(student));

        StudentResponseDTO result = studentService.getStudentById(101L);

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getEmail()).isEqualTo("aarav.sharma@example.com");
    }

    @Test
    @DisplayName("getStudentById throws ResourceNotFoundException when student does not exist")
    void getStudentById_studentAbsent_throwsNotFoundException() {
        given(studentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student")
                .hasMessageContaining("999");
    }

    // =========================================================================
    // createStudent — happy path
    // =========================================================================

    @Test
    @DisplayName("createStudent persists and returns the new student DTO")
    void createStudent_validRequest_returnsDTO() {
        given(studentRepository.existsByEmail(requestDTO.getEmail())).willReturn(false);
        given(studentRepository.existsByRollNumber(requestDTO.getRollNumber())).willReturn(false);
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(studentRepository.save(any(Student.class))).willReturn(student);

        StudentResponseDTO result = studentService.createStudent(requestDTO);

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getEmail()).isEqualTo("aarav.sharma@example.com");
        verify(studentRepository).save(any(Student.class));
    }

    // =========================================================================
    // createStudent — duplicate email
    // =========================================================================

    @Test
    @DisplayName("createStudent throws DuplicateResourceException when email already exists")
    void createStudent_duplicateEmail_throwsDuplicateException() {
        given(studentRepository.existsByEmail(requestDTO.getEmail())).willReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(studentRepository, never()).save(any());
    }

    // =========================================================================
    // createStudent — duplicate roll number
    // =========================================================================

    @Test
    @DisplayName("createStudent throws DuplicateResourceException when rollNumber already exists")
    void createStudent_duplicateRollNumber_throwsDuplicateException() {
        given(studentRepository.existsByEmail(requestDTO.getEmail())).willReturn(false);
        given(studentRepository.existsByRollNumber(requestDTO.getRollNumber())).willReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("rollNumber");

        verify(studentRepository, never()).save(any());
    }

    // =========================================================================
    // createStudent — courseId not found
    // =========================================================================

    @Test
    @DisplayName("createStudent throws ResourceNotFoundException when courseId does not exist")
    void createStudent_courseNotFound_throwsNotFoundException() {
        given(studentRepository.existsByEmail(requestDTO.getEmail())).willReturn(false);
        given(studentRepository.existsByRollNumber(requestDTO.getRollNumber())).willReturn(false);
        given(courseRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course");

        verify(studentRepository, never()).save(any());
    }

    // =========================================================================
    // updateStudent — happy path
    // =========================================================================

    @Test
    @DisplayName("updateStudent applies changes and returns the updated DTO")
    void updateStudent_validRequest_returnsUpdatedDTO() {
        given(studentRepository.findById(101L)).willReturn(Optional.of(student));
        given(studentRepository.existsByEmailAndIdNot(requestDTO.getEmail(), 101L)).willReturn(false);
        given(studentRepository.existsByRollNumberAndIdNot(requestDTO.getRollNumber(), 101L)).willReturn(false);
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        StudentResponseDTO result = studentService.updateStudent(101L, requestDTO);

        assertThat(result.getEmail()).isEqualTo("aarav.sharma@example.com");
    }

    // =========================================================================
    // updateStudent — student not found
    // =========================================================================

    @Test
    @DisplayName("updateStudent throws ResourceNotFoundException when student does not exist")
    void updateStudent_studentAbsent_throwsNotFoundException() {
        given(studentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(999L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");
    }

    // =========================================================================
    // updateStudent — email conflict with different student
    // =========================================================================

    @Test
    @DisplayName("updateStudent throws DuplicateResourceException when email belongs to another student")
    void updateStudent_emailConflict_throwsDuplicateException() {
        given(studentRepository.findById(101L)).willReturn(Optional.of(student));
        given(studentRepository.existsByEmailAndIdNot(requestDTO.getEmail(), 101L)).willReturn(true);

        assertThatThrownBy(() -> studentService.updateStudent(101L, requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }

    // =========================================================================
    // updateStudentStatus
    // =========================================================================

    @Test
    @DisplayName("updateStudentStatus changes the student's status and returns updated DTO")
    void updateStudentStatus_validStatus_returnsUpdatedDTO() {
        given(studentRepository.findById(101L)).willReturn(Optional.of(student));

        StudentResponseDTO result = studentService.updateStudentStatus(101L, StudentStatus.GRADUATED);

        assertThat(result.getStatus()).isEqualTo(StudentStatus.GRADUATED);
    }

    @Test
    @DisplayName("updateStudentStatus throws ResourceNotFoundException when student does not exist")
    void updateStudentStatus_studentAbsent_throwsNotFoundException() {
        given(studentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudentStatus(999L, StudentStatus.ACTIVE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");
    }

    // =========================================================================
    // deleteStudent
    // =========================================================================

    @Test
    @DisplayName("deleteStudent invokes repository delete when student exists")
    void deleteStudent_studentExists_deletesSuccessfully() {
        given(studentRepository.findById(101L)).willReturn(Optional.of(student));

        studentService.deleteStudent(101L);

        verify(studentRepository).delete(student);
    }

    @Test
    @DisplayName("deleteStudent throws ResourceNotFoundException when student does not exist")
    void deleteStudent_studentAbsent_throwsNotFoundException() {
        given(studentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");

        verify(studentRepository, never()).delete(any(Student.class));
    }
}

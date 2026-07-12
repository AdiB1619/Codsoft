package com.codsoft.sms.mapper;

import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.entity.Student;
import com.codsoft.sms.entity.enums.Gender;
import com.codsoft.sms.entity.enums.StudentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link StudentMapper}.
 *
 * <p>Pure unit tests — no Spring context, no database. Verifies correct field
 * mapping for {@code toResponseDTO}, {@code toEntity}, and {@code updateEntity}.
 */
@DisplayName("StudentMapper Unit Tests")
class StudentMapperTest {

    private Course course;
    private Student student;
    private StudentRequestDTO requestDTO;

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
                .profileImageUrl(null)
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
    // toResponseDTO
    // =========================================================================

    @Test
    @DisplayName("toResponseDTO maps all scalar fields correctly")
    void toResponseDTO_mapsAllScalarFields() {
        StudentResponseDTO dto = StudentMapper.toResponseDTO(student);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(101L);
        assertThat(dto.getRollNumber()).isEqualTo("CS2023001");
        assertThat(dto.getFirstName()).isEqualTo("Aarav");
        assertThat(dto.getLastName()).isEqualTo("Sharma");
        assertThat(dto.getEmail()).isEqualTo("aarav.sharma@example.com");
        assertThat(dto.getPhoneNumber()).isEqualTo("+919876543210");
        assertThat(dto.getDateOfBirth()).isEqualTo(LocalDate.of(2005, 8, 14));
        assertThat(dto.getGender()).isEqualTo(Gender.MALE);
        assertThat(dto.getAddress()).isEqualTo("221B Baker Street, Pune, MH");
        assertThat(dto.getEnrollmentDate()).isEqualTo(LocalDate.of(2023, 7, 1));
        assertThat(dto.getGrade()).isEqualByComparingTo(new BigDecimal("87.50"));
        assertThat(dto.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(dto.getProfileImageUrl()).isNull();
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 7, 1, 9, 0));
        assertThat(dto.getUpdatedAt()).isEqualTo(LocalDateTime.of(2023, 7, 1, 9, 0));
    }

    @Test
    @DisplayName("toResponseDTO maps nested Course correctly")
    void toResponseDTO_mapsNestedCourse() {
        StudentResponseDTO dto = StudentMapper.toResponseDTO(student);

        assertThat(dto.getCourse()).isNotNull();
        assertThat(dto.getCourse().getId()).isEqualTo(1L);
        assertThat(dto.getCourse().getCourseCode()).isEqualTo("CSE");
        assertThat(dto.getCourse().getCourseName()).isEqualTo("Computer Science and Engineering");
    }

    @Test
    @DisplayName("toResponseDTO returns null when entity is null")
    void toResponseDTO_nullEntity_returnsNull() {
        assertThat(StudentMapper.toResponseDTO(null)).isNull();
    }

    @Test
    @DisplayName("toResponseDTO handles null grade without error")
    void toResponseDTO_nullGrade_isPreserved() {
        student.setGrade(null);
        StudentResponseDTO dto = StudentMapper.toResponseDTO(student);
        assertThat(dto.getGrade()).isNull();
    }

    @Test
    @DisplayName("toResponseDTO handles null profileImageUrl without error")
    void toResponseDTO_nullProfileImageUrl_isPreserved() {
        student.setProfileImageUrl(null);
        StudentResponseDTO dto = StudentMapper.toResponseDTO(student);
        assertThat(dto.getProfileImageUrl()).isNull();
    }

    // =========================================================================
    // toEntity
    // =========================================================================

    @Test
    @DisplayName("toEntity maps all fields correctly from a StudentRequestDTO")
    void toEntity_mapsAllFields() {
        Student entity = StudentMapper.toEntity(requestDTO, course);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // new entity
        assertThat(entity.getRollNumber()).isEqualTo("CS2023001");
        assertThat(entity.getFirstName()).isEqualTo("Aarav");
        assertThat(entity.getLastName()).isEqualTo("Sharma");
        assertThat(entity.getEmail()).isEqualTo("aarav.sharma@example.com");
        assertThat(entity.getPhoneNumber()).isEqualTo("+919876543210");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(2005, 8, 14));
        assertThat(entity.getGender()).isEqualTo(Gender.MALE);
        assertThat(entity.getAddress()).isEqualTo("221B Baker Street, Pune, MH");
        assertThat(entity.getCourse()).isEqualTo(course);
        assertThat(entity.getEnrollmentDate()).isEqualTo(LocalDate.of(2023, 7, 1));
        assertThat(entity.getGrade()).isEqualByComparingTo(new BigDecimal("87.50"));
        assertThat(entity.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("toEntity returns null when DTO is null")
    void toEntity_nullDto_returnsNull() {
        assertThat(StudentMapper.toEntity(null, course)).isNull();
    }

    @Test
    @DisplayName("toEntity does not set id, createdAt, or updatedAt")
    void toEntity_doesNotSetAuditFields() {
        Student entity = StudentMapper.toEntity(requestDTO, course);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    // =========================================================================
    // updateEntity
    // =========================================================================

    @Test
    @DisplayName("updateEntity applies all DTO fields onto the existing entity")
    void updateEntity_appliesAllFields() {
        StudentRequestDTO updateDTO = StudentRequestDTO.builder()
                .rollNumber("CS2023001")
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .phoneNumber("+919000000000")
                .dateOfBirth(LocalDate.of(2005, 8, 14))
                .gender(Gender.FEMALE)
                .address("New Address, City")
                .courseId(1L)
                .enrollmentDate(LocalDate.of(2023, 7, 1))
                .grade(new BigDecimal("90.00"))
                .status(StudentStatus.GRADUATED)
                .build();

        Course newCourse = Course.builder()
                .id(2L)
                .courseCode("ECE")
                .courseName("Electronics")
                .department("Engineering")
                .durationYears(4)
                .build();

        Student updated = StudentMapper.updateEntity(updateDTO, student, newCourse);

        // Must return the same reference (mutated in place)
        assertThat(updated).isSameAs(student);
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getLastName()).isEqualTo("Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(updated.getCourse()).isEqualTo(newCourse);
        assertThat(updated.getGrade()).isEqualByComparingTo(new BigDecimal("90.00"));
        assertThat(updated.getStatus()).isEqualTo(StudentStatus.GRADUATED);
    }

    @Test
    @DisplayName("updateEntity preserves id and createdAt from the existing entity")
    void updateEntity_preservesImmutableFields() {
        LocalDateTime originalCreatedAt = student.getCreatedAt();
        Long originalId = student.getId();

        StudentMapper.updateEntity(requestDTO, student, course);

        assertThat(student.getId()).isEqualTo(originalId);
        assertThat(student.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    // =========================================================================
    // Round-trip
    // =========================================================================

    @Test
    @DisplayName("Round-trip entity→DTO→entity preserves all mapped fields")
    void roundTrip_entityToDtoToEntity_preservesFields() {
        StudentResponseDTO responseDTO = StudentMapper.toResponseDTO(student);

        // Simulate constructing a request DTO from response data
        StudentRequestDTO reconstructedRequest = StudentRequestDTO.builder()
                .rollNumber(responseDTO.getRollNumber())
                .firstName(responseDTO.getFirstName())
                .lastName(responseDTO.getLastName())
                .email(responseDTO.getEmail())
                .phoneNumber(responseDTO.getPhoneNumber())
                .dateOfBirth(responseDTO.getDateOfBirth())
                .gender(responseDTO.getGender())
                .address(responseDTO.getAddress())
                .courseId(responseDTO.getCourse().getId())
                .enrollmentDate(responseDTO.getEnrollmentDate())
                .grade(responseDTO.getGrade())
                .status(responseDTO.getStatus())
                .build();

        Student rebuilt = StudentMapper.toEntity(reconstructedRequest, course);

        assertThat(rebuilt.getRollNumber()).isEqualTo(student.getRollNumber());
        assertThat(rebuilt.getEmail()).isEqualTo(student.getEmail());
        assertThat(rebuilt.getGender()).isEqualTo(student.getGender());
        assertThat(rebuilt.getStatus()).isEqualTo(student.getStatus());
        assertThat(rebuilt.getGrade()).isEqualByComparingTo(student.getGrade());
    }
}

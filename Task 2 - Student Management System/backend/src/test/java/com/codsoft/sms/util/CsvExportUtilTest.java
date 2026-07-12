package com.codsoft.sms.util;

import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.enums.StudentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvExportUtil Tests")
class CsvExportUtilTest {

    @Test
    @DisplayName("Successfully writes student list to CSV and escapes special characters")
    void writeStudentsToCsv_success() throws IOException {
        StringWriter writer = new StringWriter();
        
        CourseResponseDTO course = CourseResponseDTO.builder()
                .id(1L)
                .courseCode("CS101")
                .courseName("Computer Science")
                .build();
                
        StudentResponseDTO student1 = StudentResponseDTO.builder()
                .id(101L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .rollNumber("CS001")
                .enrollmentDate(LocalDate.of(2023, 9, 1))
                .status(StudentStatus.ACTIVE)
                .course(course)
                .build();
                
        // Test escaping of commas and quotes
        StudentResponseDTO student2 = StudentResponseDTO.builder()
                .id(102L)
                .firstName("Jane, \"Jr.\"")
                .lastName("Smith")
                .email("jane@example.com")
                .rollNumber("CS002")
                .status(StudentStatus.GRADUATED)
                .build();

        CsvExportUtil.writeStudentsToCsv(writer, List.of(student1, student2));
        
        String result = writer.toString();
        String[] lines = result.split("\n");
        
        assertThat(lines).hasSize(3); // Header + 2 rows
        
        // Check header
        assertThat(lines[0]).isEqualTo("ID,First Name,Last Name,Email,Roll Number,Course,Enrollment Date,Grade,Status");
        
        // Check standard row
        assertThat(lines[1]).isEqualTo("101,John,Doe,john@example.com,CS001,CS101,2023-09-01,,ACTIVE");
        
        // Check escaped row
        assertThat(lines[2]).isEqualTo("102,\"Jane, \"\"Jr.\"\"\",Smith,jane@example.com,CS002,,,,GRADUATED");
    }
}

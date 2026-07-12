package com.codsoft.sms.util;

import com.codsoft.sms.dto.response.StudentResponseDTO;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Utility for generating CSV exports.
 */
public final class CsvExportUtil {

    private CsvExportUtil() {
        // Utility class
    }

    private static final String[] HEADERS = {
            "ID", "First Name", "Last Name", "Email", "Roll Number", 
            "Course", "Enrollment Date", "Grade", "Status"
    };

    /**
     * Streams the given list of students to a CSV writer, properly escaping
     * fields that contain commas, quotes, or newlines.
     *
     * @param writer   the output writer (e.g. from HttpServletResponse)
     * @param students the list of students to export
     * @throws IOException if a writing error occurs
     */
    public static void writeStudentsToCsv(Writer writer, List<StudentResponseDTO> students) throws IOException {
        // Write headers
        writer.write(String.join(",", HEADERS) + "\n");

        // Write rows
        for (StudentResponseDTO s : students) {
            String[] row = {
                    String.valueOf(s.getId()),
                    escape(s.getFirstName()),
                    escape(s.getLastName()),
                    escape(s.getEmail()),
                    escape(s.getRollNumber()),
                    s.getCourse() != null ? escape(s.getCourse().getCourseCode()) : "",
                    s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "",
                    s.getGrade() != null ? s.getGrade().toString() : "",
                    s.getStatus() != null ? s.getStatus().name() : ""
            };
            writer.write(String.join(",", row) + "\n");
        }
        writer.flush();
    }

    /**
     * Escapes a CSV field according to RFC 4180 rules.
     * - If the field contains commas, quotes, or newlines, it is enclosed in double quotes.
     * - Any double quote inside the field is escaped by a preceding double quote.
     */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean containsQuote = value.contains("\"");
        boolean containsComma = value.contains(",");
        boolean containsNewline = value.contains("\n") || value.contains("\r");

        if (containsQuote || containsComma || containsNewline) {
            String escaped = value.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return value;
    }
}

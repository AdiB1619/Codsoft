package com.codsoft.sms.controller;

import com.codsoft.sms.dto.request.StatusUpdateRequest;
import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.ApiResponse;
import com.codsoft.sms.dto.response.PagedResponse;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.codsoft.sms.service.StudentService;
import com.codsoft.sms.util.AppConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for student-related endpoints.
 *
 * <p>Base path: {@code /api/v1/students} (per SDD Section 7.1).
 *
 * <p>Endpoints implemented in this class (v1.0 scope per this prompt):
 * <ul>
 *   <li>{@code POST   /api/v1/students}                  — create student (§7.3)</li>
 *   <li>{@code GET    /api/v1/students}                   — list students, paginated (§7.4)</li>
 *   <li>{@code GET    /api/v1/students/{id}}              — get one student (§7.5)</li>
 *   <li>{@code PUT    /api/v1/students/{id}}              — full update (§7.6)</li>
 *   <li>{@code PATCH  /api/v1/students/{id}/status}       — status-only update (§7.7)</li>
 *   <li>{@code DELETE /api/v1/students/{id}}              — delete (§7.8)</li>
 * </ul>
 *
 * <p>File-upload (§7.9), image-delete (§7.10), and CSV-export (§7.11) are
 * deferred to later prompts as specified in the task description.
 *
 * <p><strong>Architecture constraints:</strong>
 * <ul>
 *   <li>Zero business logic in this class — all rules live in
 *       {@link com.codsoft.sms.service.StudentService}.</li>
 *   <li>HTTP binding (path variables, query params, request bodies) is the
 *       sole responsibility of each handler method.</li>
 *   <li>Repositories are never injected here.</li>
 *   <li>The raw {@code sortBy} string is validated against a whitelist before
 *       reaching the service, per SDD §7.4 ("never silently falls back").</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/students  — §7.3  (201 Created)
    // -------------------------------------------------------------------------

    /**
     * Creates a new student.
     *
     * @param requestDTO the validated student payload
     * @return {@code 201 Created} with the persisted student
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO created = studentService.createStudent(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", created));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/students  — §7.4  (200 OK)
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated, optionally filtered and searched list of students.
     *
     * <p>The {@code sortBy} parameter is validated against
     * {@link AppConstants#ALLOWED_SORT_FIELDS}. Unknown values return
     * {@code 400 Bad Request} — the raw string is never passed to JPA.
     *
     * @param page     zero-based page index (default 0)
     * @param size     items per page, 1–100 (default 10)
     * @param sortBy   field to sort by — must be one of the allowed values (default "id")
     * @param sortDir  direction: "asc" or "desc" (default "asc")
     * @param search   optional partial-match filter on name/email/rollNumber
     * @param courseId optional course filter
     * @param status   optional status filter
     * @return {@code 200 OK} with a {@link PagedResponse} of student DTOs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> getStudents(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) StudentStatus status) {

        // Validate sortBy against the whitelist — never pass raw user input to JPA.
        if (!AppConstants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy value '" + sortBy + "'. Allowed: " + AppConstants.ALLOWED_SORT_FIELDS
            );
        }

        // Clamp page size to the configured maximum.
        int clampedSize = Math.min(size, AppConstants.MAX_PAGE_SIZE);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, clampedSize, sort);
        PagedResponse<StudentResponseDTO> pagedResult =
                studentService.getStudents(search, courseId, status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Students retrieved successfully", pagedResult));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/students/stats
    // -------------------------------------------------------------------------

    /**
     * Retrieves aggregated statistics for the dashboard.
     *
     * @return {@code 200 OK} with DashboardStatsDTO
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<com.codsoft.sms.dto.response.DashboardStatsDTO>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard stats fetched successfully",
                studentService.getDashboardStats()
        ));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/students/{id}  — §7.5  (200 OK | 404)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single student's full profile.
     *
     * @param id the student's database ID
     * @return {@code 200 OK} with the student DTO, or {@code 404} if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(
            @PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/students/{id}  — §7.6  (200 OK | 400 | 404 | 409)
    // -------------------------------------------------------------------------

    /**
     * Fully replaces an existing student's data.
     *
     * @param id         the student's database ID
     * @param requestDTO the validated replacement data
     * @return {@code 200 OK} with the updated student DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO updated = studentService.updateStudent(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));
    }

    // -------------------------------------------------------------------------
    // PATCH /api/v1/students/{id}/status  — §7.7  (200 OK | 400 | 404)
    // -------------------------------------------------------------------------

    /**
     * Changes only the enrollment status of an existing student.
     *
     * <p>Jackson rejects unknown enum values before the method body executes,
     * automatically returning {@code 400 Bad Request}.
     *
     * @param id      the student's database ID
     * @param request the status payload
     * @return {@code 200 OK} with the updated student DTO
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudentStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        StudentResponseDTO updated = studentService.updateStudentStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Student status updated successfully", updated));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/students/{id}  — §7.8  (204 No Content | 404)
    // -------------------------------------------------------------------------

    /**
     * Permanently removes a student record.
     *
     * @param id the student's database ID
     * @return {@code 204 No Content} on success, or {@code 404} if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // GET /api/v1/students/export  — §7.11
    // =========================================================================

    /**
     * Exports the filtered list of students as a CSV file.
     *
     * @param search   optional free-text search term
     * @param courseId optional course filter
     * @param status   optional status filter
     * @param sortBy   the field to sort by
     * @param sortDir  the sort direction (asc/desc)
     * @param response the HttpServletResponse to stream the CSV to
     */
    @GetMapping("/export")
    public void exportStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir,
            HttpServletResponse response) throws IOException {

        // 1. Validate sort fields to prevent SQL injection or 500s
        if (!AppConstants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortBy field: " + sortBy);
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // 2. Parse status explicitly since it's an enum
        StudentStatus studentStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                studentStatus = StudentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + status);
            }
        }

        // 3. Set headers for CSV download
        response.setContentType("text/csv");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setHeader("Content-Disposition", "attachment; filename=\"students_export_" + timestamp + ".csv\"");

        // 4. Fetch and stream data
        List<com.codsoft.sms.dto.response.StudentResponseDTO> students = 
            studentService.getStudentsForExport(search, courseId, studentStatus, sort);
        
        com.codsoft.sms.util.CsvExportUtil.writeStudentsToCsv(response.getWriter(), students);
    }

    // =========================================================================
    // POST /api/v1/students/{id}/profile-image  — §7.9
    // =========================================================================

    /**
     * Uploads or replaces a student's profile image.
     *
     * @param id   the primary key of the student
     * @param file the multipart file (must be JPG/PNG and under 2MB)
     * @return {@code 200 OK} with the URL of the uploaded image
     */
    @PostMapping(value = "/{id}/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        StudentResponseDTO updatedStudent = studentService.uploadProfileImage(id, file);

        return ResponseEntity.ok(ApiResponse.success(
                "Profile image uploaded successfully",
                Map.of("profileImageUrl", updatedStudent.getProfileImageUrl())
        ));
    }

    // =========================================================================
    // DELETE /api/v1/students/{id}/profile-image  — §7.10
    // =========================================================================

    /**
     * Removes a student's profile image.
     *
     * @param id the primary key of the student
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<Void> removeProfileImage(@PathVariable Long id) {
        studentService.removeProfileImage(id);
        return ResponseEntity.noContent().build();
    }
}

package com.codsoft.sms.controller;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.ApiResponse;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for course-related endpoints.
 *
 * <p>Base path: {@code /api/v1/courses} (per SDD Section 7.1).
 *
 * <p>Endpoints implemented in this class (v1.0 scope):
 * <ul>
 *   <li>{@code GET  /api/v1/courses}  — list all courses (§7.12)</li>
 *   <li>{@code POST /api/v1/courses}  — create a new course (§7.13)</li>
 * </ul>
 *
 * <p><strong>Architecture constraints:</strong>
 * <ul>
 *   <li>This class contains <em>zero</em> business logic — all rules live in
 *       {@link com.codsoft.sms.service.CourseService}.</li>
 *   <li>HTTP binding (path variables, request parameters, request bodies) is
 *       the sole responsibility of each handler method.</li>
 *   <li>Repositories are never injected here — access is exclusively through the service.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/courses  — §7.12
    // -------------------------------------------------------------------------

    /**
     * Lists all available courses.
     *
     * <p>Primarily used to populate course dropdowns and filters in the UI.
     *
     * @return {@code 200 OK} with the list of all courses wrapped in {@link ApiResponse}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/courses  — §7.13
    // -------------------------------------------------------------------------

    /**
     * Creates a new course.
     *
     * <p>Returns {@code 409 Conflict} (via {@link com.codsoft.sms.exception.GlobalExceptionHandler})
     * when the supplied {@code courseCode} already exists.
     *
     * @param requestDTO the validated course payload
     * @return {@code 201 Created} with the persisted course wrapped in {@link ApiResponse}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDTO>> createCourse(
            @Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO created = courseService.createCourse(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", created));
    }
}

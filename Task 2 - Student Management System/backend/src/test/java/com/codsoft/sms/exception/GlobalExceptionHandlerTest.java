package com.codsoft.sms.exception;

import com.codsoft.sms.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Verifies that all exception types defined in SDD Section 11 map to the
 * correct HTTP status code and construct the proper {@link ErrorResponse} shape.
 * Validation (MethodArgumentNotValidException) is tested implicitly via the
 * WebMvcTests in the controller package.
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/v1/test");
    }

    @Test
    @DisplayName("ResourceNotFoundException maps to 404 Not Found")
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Student", "id", 1L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Student not found with id: '1'");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
    }

    @Test
    @DisplayName("DuplicateResourceException maps to 409 Conflict")
    void handleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Student", "email", "test@test.com");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).isEqualTo("Student already exists with email: 'test@test.com'");
    }

    @Test
    @DisplayName("DataIntegrityViolationException maps to 409 Conflict")
    void handleDataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("database constraint");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException maps to 400 Bad Request")
    void handleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON request body.");
    }

    @Test
    @DisplayName("InvalidFileException maps to 400 Bad Request")
    void handleInvalidFileException() {
        InvalidFileException ex = new InvalidFileException("File too large");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidFileException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("File too large");
    }

    @Test
    @DisplayName("ResponseStatusException maps to its encapsulated status")
    void handleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResponseStatusException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody().getStatus()).isEqualTo(415);
        assertThat(response.getBody().getMessage()).isEqualTo("Unsupported");
    }

    @Test
    @DisplayName("FileStorageException maps to 500 Internal Server Error")
    void handleFileStorageException() {
        FileStorageException ex = new FileStorageException("Disk full", new RuntimeException());
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileStorageException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).contains("Failed to process the uploaded file");
    }

    @Test
    @DisplayName("Generic Exception maps to 500 Internal Server Error")
    void handleGenericException() {
        Exception ex = new RuntimeException("Something exploded");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
    }
}

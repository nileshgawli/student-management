package com.aurionpro.studentmanagement.exception;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * A centralized exception handler for the entire application.
 * This class uses {@link ControllerAdvice} to intercept exceptions thrown from any controller
 * and formats them into a standardized {@link ApiResponse} object.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ValidationException}.
     * This is triggered by custom service-level validation failures.
     *
     * @param ex The caught ValidationException.
     * @return A ResponseEntity with a 400 Bad Request status, containing a summary and a list of errors.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("summary", ex.getMessage());
        errorDetails.put("errors", ex.getErrors());
        ApiResponse<Object> response = new ApiResponse<>("error", "Validation Failed", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     * This occurs when an entity lookup by ID or another unique key fails.
     *
     * @param ex The caught ResourceNotFoundException.
     * @return A ResponseEntity with a 404 Not Found status and an error message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link DuplicateResourceException}.
     * This is thrown when an attempt to create a resource violates a unique constraint.
     *
     * @param ex The caught DuplicateResourceException.
     * @return A ResponseEntity with a 409 Conflict status and an error message.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Handles {@link BusinessRuleException}.
     * This is a general handler for violations of application-specific business rules.
     *
     * @param ex The caught BusinessRuleException.
     * @return A ResponseEntity with a 400 Bad Request status and an error message.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRuleException(BusinessRuleException ex) {
        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link MethodArgumentNotValidException}.
     * This is thrown automatically by Spring Boot when DTOs annotated with {@code @Valid} fail validation.
     *
     * @param ex The caught MethodArgumentNotValidException.
     * @return A ResponseEntity with a 400 Bad Request status, containing a map of field-specific errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Object> response = new ApiResponse<>("error", "Input validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link DataIntegrityViolationException}.
     * This is a generic handler for database-level integrity constraint violations, often
     * caused by duplicate unique keys that were not caught at the service level.
     *
     * @param ex The caught DataIntegrityViolationException.
     * @return A ResponseEntity with a 409 Conflict status and a user-friendly error message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "A database constraint was violated. This may be due to a duplicate entry.";
        // Provide a more specific message if the cause is a known unique constraint violation
        if (ex.getMostSpecificCause().getMessage().contains("duplicate key value violates unique constraint")) {
            message = "A resource with a provided unique field (like studentId or email) already exists.";
        }
        ApiResponse<Object> response = new ApiResponse<>("error", message, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
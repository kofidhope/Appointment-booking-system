package com.kofi.booking_system.common.exception;

import com.kofi.booking_system.common.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors (DTO @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid request payload")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // Duplicate resources (email exists, etc)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Resource Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Invalid credentials / auth errors
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex,
                                            HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<?> handleConflict(BookingConflictException ex,
                                            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenActionException ex,
                                             HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), null, request.getRequestURI());
    }

    // Fallback (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Something went wrong. Please try again.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status,
                                                           String message,
                                                           Object errors,
                                                           String path) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        if (errors != null) {
            body.put("errors", errors);
        }

        return ResponseEntity.status(status).body(body);
    }
}

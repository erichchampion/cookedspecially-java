package com.cookedspecially.notificationservice.controller;

import com.cookedspecially.notificationservice.dto.ErrorResponse;
import com.cookedspecially.notificationservice.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle notification not found
     */
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotFound(
        NotificationNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Notification not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Notification Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle template not found
     */
    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemplateNotFound(
        TemplateNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Template not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Template Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle notification send exception
     */
    @ExceptionHandler(NotificationSendException.class)
    public ResponseEntity<ErrorResponse> handleNotificationSendException(
        NotificationSendException ex,
        HttpServletRequest request
    ) {
        log.error("Notification send failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Notification Send Failed")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle rate limit exceeded
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
        RateLimitExceededException ex,
        HttpServletRequest request
    ) {
        log.error("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.TOO_MANY_REQUESTS.value())
            .error("Rate Limit Exceeded")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        log.error("Validation error: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ErrorResponse.FieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            ));
        }

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Request validation failed")
            .path(request.getRequestURI())
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex,
        HttpServletRequest request
    ) {
        log.error("Access denied: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("You do not have permission to access this resource")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle illegal argument
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Request")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later.")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

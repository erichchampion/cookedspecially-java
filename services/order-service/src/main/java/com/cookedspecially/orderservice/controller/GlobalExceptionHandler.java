package com.cookedspecially.orderservice.controller;

import com.cookedspecially.orderservice.dto.ErrorResponse;
import com.cookedspecially.orderservice.exception.InvalidOrderStateException;
import com.cookedspecially.orderservice.exception.OrderNotFoundException;
import com.cookedspecially.orderservice.exception.OrderValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler for Order Service
 *
 * Handles exceptions thrown by controllers and converts them to appropriate HTTP responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle OrderNotFoundException
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
            OrderNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Order not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle InvalidOrderStateException
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStateException(
            InvalidOrderStateException ex,
            HttpServletRequest request) {
        log.warn("Invalid order state transition: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle OrderValidationException
     */
    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ErrorResponse> handleOrderValidationException(
            OrderValidationException ex,
            HttpServletRequest request) {
        log.warn("Order validation failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getRequestURI()
        );

        if (ex.getField() != null) {
            List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
            validationErrors.add(ErrorResponse.ValidationError.builder()
                .field(ex.getField())
                .message(ex.getMessage())
                .build());
            error.setValidationErrors(validationErrors);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle MethodArgumentNotValidException (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Validation failed for request: {}", ex.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError
                ? ((FieldError) error).getField()
                : error.getObjectName();
            String errorMessage = error.getDefaultMessage();

            validationErrors.add(ErrorResponse.ValidationError.builder()
                .field(fieldName)
                .message(errorMessage)
                .build());
        });

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(java.time.LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message("Invalid request parameters")
            .path(request.getRequestURI())
            .validationErrors(validationErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error occurred: ", ex);

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

package com.cookedspecially.paymentservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.paymentservice.dto.ErrorResponse;
import com.cookedspecially.paymentservice.exception.PaymentNotFoundException;
import com.cookedspecially.paymentservice.exception.PaymentProcessingException;
import com.cookedspecially.paymentservice.exception.RefundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler for Payment Service
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle PaymentNotFoundException
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(
            PaymentNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Payment not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle PaymentProcessingException
     */
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(
            PaymentProcessingException ex,
            HttpServletRequest request) {
        log.error("Payment processing failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Payment Processing Failed",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle RefundException
     */
    @ExceptionHandler(RefundException.class)
    public ResponseEntity<ErrorResponse> handleRefundException(
            RefundException ex,
            HttpServletRequest request) {
        log.error("Refund processing failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Refund Processing Failed",
            ex.getMessage(),
            request.getRequestURI()
        );

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

            ErrorResponse.ValidationError validationError = new ErrorResponse.ValidationError();
            validationError.setField(fieldName);
            validationError.setMessage(errorMessage);
            validationErrors.add(validationError);
        });

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(java.time.LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Validation Error");
        error.setMessage("Invalid request parameters");
        error.setPath(request.getRequestURI());
        error.setValidationErrors(validationErrors);

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

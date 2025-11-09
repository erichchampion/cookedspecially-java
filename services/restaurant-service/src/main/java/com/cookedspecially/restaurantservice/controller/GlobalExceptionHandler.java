package com.cookedspecially.restaurantservice.controller;

import com.cookedspecially.restaurantservice.dto.ErrorResponse;
import com.cookedspecially.restaurantservice.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
     * Handle restaurant not found
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(
        RestaurantNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Restaurant not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Restaurant Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle menu item not found
     */
    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMenuItemNotFound(
        MenuItemNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Menu item not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Menu Item Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle invalid restaurant state
     */
    @ExceptionHandler(InvalidRestaurantStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRestaurantState(
        InvalidRestaurantStateException ex,
        HttpServletRequest request
    ) {
        log.error("Invalid restaurant state: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Restaurant State")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle unauthorized access
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(
        UnauthorizedAccessException ex,
        HttpServletRequest request
    ) {
        log.error("Unauthorized access: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Unauthorized Access")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle image upload exception
     */
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(
        ImageUploadException ex,
        HttpServletRequest request
    ) {
        log.error("Image upload failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Image Upload Failed")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle max upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
        MaxUploadSizeExceededException ex,
        HttpServletRequest request
    ) {
        log.error("Max upload size exceeded: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("File Too Large")
            .message("Upload file size exceeds maximum allowed size")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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

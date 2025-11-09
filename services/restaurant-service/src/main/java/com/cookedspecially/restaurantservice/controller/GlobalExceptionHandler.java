package com.cookedspecially.restaurantservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.restaurantservice.dto.ErrorResponse;
import com.cookedspecially.restaurantservice.exception.*;
import jakarta.servlet.http.HttpServletRequest;
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
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle restaurant not found
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(
        RestaurantNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Restaurant not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Restaurant Not Found",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Menu Item Not Found",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Restaurant State",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Unauthorized Access",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Image Upload Failed",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "File Too Large",
            "Upload file size exceeds maximum allowed size",
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Request validation failed",
            request.getRequestURI(),
            fieldErrors
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            "You do not have permission to access this resource",
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Request",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );

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

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI(),
            null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

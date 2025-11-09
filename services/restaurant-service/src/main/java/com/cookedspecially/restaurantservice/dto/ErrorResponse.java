package com.cookedspecially.restaurantservice.dto;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Error Response DTO
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldError> fieldErrors
) {
    public record FieldError(
        String field,
        String message
    ) {}
}

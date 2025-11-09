package com.cookedspecially.restaurantservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Error Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;

    private String error;

    private String message;

    private String path;

    private List<FieldError> fieldErrors;

    /**
     * Field error details
     */
    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}

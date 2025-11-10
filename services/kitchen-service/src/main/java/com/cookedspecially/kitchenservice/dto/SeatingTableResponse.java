package com.cookedspecially.kitchenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for seating table information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatingTableResponse {
    private Long id;
    private String tableNumber;
    private String name;
    private Long restaurantId;
    private Long fulfillmentCenterId;
    private Integer capacity;
    private String status;
    private String section;
    private String qrCode;
    private Long currentOrderId;
    private LocalDateTime occupiedSince;
    private Boolean active;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

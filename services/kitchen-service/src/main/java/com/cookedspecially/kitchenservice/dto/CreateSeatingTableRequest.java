package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a seating table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeatingTableRequest {

    @NotBlank(message = "Table number is required")
    private String tableNumber;

    private String name;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Fulfillment center ID is required")
    private Long fulfillmentCenterId;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity = 4;

    private String section;

    private String notes;
}

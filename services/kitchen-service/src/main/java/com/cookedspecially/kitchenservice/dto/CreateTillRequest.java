package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a till
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTillRequest {

    @NotBlank(message = "Till name is required")
    private String name;

    private String description;

    @NotNull(message = "Fulfillment center ID is required")
    private Long fulfillmentCenterId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}

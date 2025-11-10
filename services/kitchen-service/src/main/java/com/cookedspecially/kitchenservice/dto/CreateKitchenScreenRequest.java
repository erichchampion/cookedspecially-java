package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a kitchen screen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKitchenScreenRequest {

    @NotBlank(message = "Screen name is required")
    private String name;

    private String description;

    @NotNull(message = "Fulfillment center ID is required")
    private Long fulfillmentCenterId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    private String stationType;

    private String ipAddress;

    private String deviceId;

    private Boolean soundEnabled = true;

    private Boolean autoAcceptOrders = false;

    private Integer displayOrder = 0;
}

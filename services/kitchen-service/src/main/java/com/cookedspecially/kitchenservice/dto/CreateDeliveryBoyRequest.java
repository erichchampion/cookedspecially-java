package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a delivery boy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryBoyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    private String vehicleType;

    private String vehicleNumber;

    private String licenseNumber;

    private String notes;
}

package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create address request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    private String addressType;  // HOME, WORK, OTHER

    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    private String streetAddress;

    @Size(max = 50, message = "Apartment/unit must not exceed 50 characters")
    private String apartmentUnit;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Size(max = 500, message = "Delivery instructions must not exceed 500 characters")
    private String deliveryInstructions;

    private Boolean isDefault;
}

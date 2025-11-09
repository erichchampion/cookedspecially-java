package com.cookedspecially.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Address response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String addressType;
    private String label;
    private String streetAddress;
    private String apartmentUnit;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String deliveryInstructions;
    private Boolean isDefault;
    private String fullAddress;
    private LocalDateTime createdAt;
}

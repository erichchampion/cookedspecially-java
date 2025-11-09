package com.cookedspecially.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer preference response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreferenceResponse {
    private Long id;
    private String dietaryRestrictions;
    private String favoriteCuisines;
    private String allergies;
    private String preferredPaymentMethod;
    private Integer defaultTipPercentage;
    private String language;
    private String currency;
    private Boolean darkMode;
    private Boolean showOnlineStatus;
    private Boolean shareLocation;
    private Boolean saveOrderHistory;
}

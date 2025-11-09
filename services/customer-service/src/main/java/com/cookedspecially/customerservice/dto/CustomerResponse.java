package com.cookedspecially.customerservice.dto;

import com.cookedspecially.customerservice.domain.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Customer response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String profileImageUrl;
    private CustomerStatus status;
    private Integer loyaltyPoints;
    private BigDecimal accountCredit;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean marketingEmails;
    private List<AddressResponse> addresses;
    private CustomerPreferenceResponse preferences;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime lastOrderDate;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

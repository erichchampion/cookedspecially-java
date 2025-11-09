package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Update customer request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY", message = "Invalid gender value")
    private String gender;

    private String profileImageUrl;

    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean marketingEmails;
}

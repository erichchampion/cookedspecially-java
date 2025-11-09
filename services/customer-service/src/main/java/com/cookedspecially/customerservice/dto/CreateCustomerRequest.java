package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Create customer request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "Cognito subject is required")
    private String cognitoSub;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY", message = "Invalid gender value")
    private String gender;

    private String profileImageUrl;
}

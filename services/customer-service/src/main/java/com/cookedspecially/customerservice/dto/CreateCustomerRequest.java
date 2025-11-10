package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Create customer request DTO
 */
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

    // Constructors
    public CreateCustomerRequest() {
    }

    public CreateCustomerRequest(String cognitoSub, String email, String phoneNumber, String firstName,
                                 String lastName, LocalDate dateOfBirth, String gender, String profileImageUrl) {
        this.cognitoSub = cognitoSub;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters
    public String getCognitoSub() {
        return cognitoSub;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Setters
    public void setCognitoSub(String cognitoSub) {
        this.cognitoSub = cognitoSub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCustomerRequest that = (CreateCustomerRequest) o;
        return Objects.equals(cognitoSub, that.cognitoSub) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phoneNumber, that.phoneNumber) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(dateOfBirth, that.dateOfBirth) &&
               Objects.equals(gender, that.gender) &&
               Objects.equals(profileImageUrl, that.profileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cognitoSub, email, phoneNumber, firstName, lastName, dateOfBirth, gender, profileImageUrl);
    }

    // toString
    @Override
    public String toString() {
        return "CreateCustomerRequest{" +
               "cognitoSub='" + cognitoSub + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               ", gender='" + gender + '\'' +
               ", profileImageUrl='" + profileImageUrl + '\'' +
               '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String cognitoSub;
        private String email;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String gender;
        private String profileImageUrl;

        public Builder cognitoSub(String cognitoSub) {
            this.cognitoSub = cognitoSub;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public CreateCustomerRequest build() {
            return new CreateCustomerRequest(cognitoSub, email, phoneNumber, firstName, lastName,
                                             dateOfBirth, gender, profileImageUrl);
        }
    }
}

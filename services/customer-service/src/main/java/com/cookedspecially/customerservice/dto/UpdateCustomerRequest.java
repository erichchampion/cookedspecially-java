package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Update customer request DTO
 */
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

    // Constructors
    public UpdateCustomerRequest() {
    }

    public UpdateCustomerRequest(String phoneNumber, String firstName, String lastName,
                                LocalDate dateOfBirth, String gender, String profileImageUrl,
                                Boolean emailNotifications, Boolean smsNotifications,
                                Boolean pushNotifications, Boolean marketingEmails) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
        this.pushNotifications = pushNotifications;
        this.marketingEmails = marketingEmails;
    }

    // Getters
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

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public Boolean getMarketingEmails() {
        return marketingEmails;
    }

    // Setters
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

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public void setMarketingEmails(Boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateCustomerRequest that = (UpdateCustomerRequest) o;
        return Objects.equals(phoneNumber, that.phoneNumber) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(dateOfBirth, that.dateOfBirth) &&
               Objects.equals(gender, that.gender) &&
               Objects.equals(profileImageUrl, that.profileImageUrl) &&
               Objects.equals(emailNotifications, that.emailNotifications) &&
               Objects.equals(smsNotifications, that.smsNotifications) &&
               Objects.equals(pushNotifications, that.pushNotifications) &&
               Objects.equals(marketingEmails, that.marketingEmails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, firstName, lastName, dateOfBirth, gender,
                          profileImageUrl, emailNotifications, smsNotifications,
                          pushNotifications, marketingEmails);
    }

    // toString
    @Override
    public String toString() {
        return "UpdateCustomerRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", emailNotifications=" + emailNotifications +
                ", smsNotifications=" + smsNotifications +
                ", pushNotifications=" + pushNotifications +
                ", marketingEmails=" + marketingEmails +
                '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String gender;
        private String profileImageUrl;
        private Boolean emailNotifications;
        private Boolean smsNotifications;
        private Boolean pushNotifications;
        private Boolean marketingEmails;

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

        public Builder emailNotifications(Boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
            return this;
        }

        public Builder smsNotifications(Boolean smsNotifications) {
            this.smsNotifications = smsNotifications;
            return this;
        }

        public Builder pushNotifications(Boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
            return this;
        }

        public Builder marketingEmails(Boolean marketingEmails) {
            this.marketingEmails = marketingEmails;
            return this;
        }

        public UpdateCustomerRequest build() {
            return new UpdateCustomerRequest(phoneNumber, firstName, lastName, dateOfBirth,
                                            gender, profileImageUrl, emailNotifications,
                                            smsNotifications, pushNotifications, marketingEmails);
        }
    }
}

package com.cookedspecially.customerservice.dto;

import com.cookedspecially.customerservice.domain.CustomerStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Customer response DTO
 */
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

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String email, String phoneNumber, String firstName, String lastName,
                          String fullName, LocalDate dateOfBirth, String gender, String profileImageUrl,
                          CustomerStatus status, Integer loyaltyPoints, BigDecimal accountCredit,
                          Boolean emailNotifications, Boolean smsNotifications, Boolean pushNotifications,
                          Boolean marketingEmails, List<AddressResponse> addresses,
                          CustomerPreferenceResponse preferences, Integer totalOrders, BigDecimal totalSpent,
                          LocalDateTime lastOrderDate, Boolean emailVerified, Boolean phoneVerified,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.loyaltyPoints = loyaltyPoints;
        this.accountCredit = accountCredit;
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
        this.pushNotifications = pushNotifications;
        this.marketingEmails = marketingEmails;
        this.addresses = addresses;
        this.preferences = preferences;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.lastOrderDate = lastOrderDate;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public BigDecimal getAccountCredit() {
        return accountCredit;
    }

    public void setAccountCredit(BigDecimal accountCredit) {
        this.accountCredit = accountCredit;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getMarketingEmails() {
        return marketingEmails;
    }

    public void setMarketingEmails(Boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
    }

    public List<AddressResponse> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressResponse> addresses) {
        this.addresses = addresses;
    }

    public CustomerPreferenceResponse getPreferences() {
        return preferences;
    }

    public void setPreferences(CustomerPreferenceResponse preferences) {
        this.preferences = preferences;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerResponse that = (CustomerResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(profileImageUrl, that.profileImageUrl) &&
                status == that.status &&
                Objects.equals(loyaltyPoints, that.loyaltyPoints) &&
                Objects.equals(accountCredit, that.accountCredit) &&
                Objects.equals(emailNotifications, that.emailNotifications) &&
                Objects.equals(smsNotifications, that.smsNotifications) &&
                Objects.equals(pushNotifications, that.pushNotifications) &&
                Objects.equals(marketingEmails, that.marketingEmails) &&
                Objects.equals(addresses, that.addresses) &&
                Objects.equals(preferences, that.preferences) &&
                Objects.equals(totalOrders, that.totalOrders) &&
                Objects.equals(totalSpent, that.totalSpent) &&
                Objects.equals(lastOrderDate, that.lastOrderDate) &&
                Objects.equals(emailVerified, that.emailVerified) &&
                Objects.equals(phoneVerified, that.phoneVerified) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, phoneNumber, firstName, lastName, fullName, dateOfBirth, gender,
                profileImageUrl, status, loyaltyPoints, accountCredit, emailNotifications, smsNotifications,
                pushNotifications, marketingEmails, addresses, preferences, totalOrders, totalSpent,
                lastOrderDate, emailVerified, phoneVerified, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "CustomerResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", status=" + status +
                ", loyaltyPoints=" + loyaltyPoints +
                ", accountCredit=" + accountCredit +
                ", emailNotifications=" + emailNotifications +
                ", smsNotifications=" + smsNotifications +
                ", pushNotifications=" + pushNotifications +
                ", marketingEmails=" + marketingEmails +
                ", addresses=" + addresses +
                ", preferences=" + preferences +
                ", totalOrders=" + totalOrders +
                ", totalSpent=" + totalSpent +
                ", lastOrderDate=" + lastOrderDate +
                ", emailVerified=" + emailVerified +
                ", phoneVerified=" + phoneVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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

        public Builder id(Long id) {
            this.id = id;
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

        public Builder fullName(String fullName) {
            this.fullName = fullName;
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

        public Builder status(CustomerStatus status) {
            this.status = status;
            return this;
        }

        public Builder loyaltyPoints(Integer loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public Builder accountCredit(BigDecimal accountCredit) {
            this.accountCredit = accountCredit;
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

        public Builder addresses(List<AddressResponse> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder preferences(CustomerPreferenceResponse preferences) {
            this.preferences = preferences;
            return this;
        }

        public Builder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public Builder totalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public Builder lastOrderDate(LocalDateTime lastOrderDate) {
            this.lastOrderDate = lastOrderDate;
            return this;
        }

        public Builder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Builder phoneVerified(Boolean phoneVerified) {
            this.phoneVerified = phoneVerified;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CustomerResponse build() {
            return new CustomerResponse(id, email, phoneNumber, firstName, lastName, fullName, dateOfBirth,
                    gender, profileImageUrl, status, loyaltyPoints, accountCredit, emailNotifications,
                    smsNotifications, pushNotifications, marketingEmails, addresses, preferences, totalOrders,
                    totalSpent, lastOrderDate, emailVerified, phoneVerified, createdAt, updatedAt);
        }
    }
}

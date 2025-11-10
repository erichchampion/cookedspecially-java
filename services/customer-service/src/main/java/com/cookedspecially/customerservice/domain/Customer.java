package com.cookedspecially.customerservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Customer entity
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_phone", columnList = "phone_number"),
    @Index(name = "idx_cognito_sub", columnList = "cognito_sub"),
    @Index(name = "idx_status", columnList = "status")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String cognitoSub;  // AWS Cognito user ID

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;  // MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomerStatus status = CustomerStatus.PENDING_VERIFICATION;

    // Loyalty and credits
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "account_credit", precision = 10, scale = 2)
    private BigDecimal accountCredit = BigDecimal.ZERO;

    // Preferences
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    private Boolean smsNotifications = true;

    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;

    @Column(name = "marketing_emails")
    private Boolean marketingEmails = false;

    // Addresses
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // Preferences
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private CustomerPreference preferences;

    // Statistics
    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    // Verification
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public Customer() {
    }

    public Customer(Long id, String cognitoSub, String email, String phoneNumber, String firstName,
                   String lastName, LocalDate dateOfBirth, String gender, String profileImageUrl,
                   CustomerStatus status, Integer loyaltyPoints, BigDecimal accountCredit,
                   Boolean emailNotifications, Boolean smsNotifications, Boolean pushNotifications,
                   Boolean marketingEmails, List<Address> addresses, CustomerPreference preferences,
                   Integer totalOrders, BigDecimal totalSpent, LocalDateTime lastOrderDate,
                   Boolean emailVerified, Boolean phoneVerified, LocalDateTime createdAt,
                   LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.cognitoSub = cognitoSub;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.status = status != null ? status : CustomerStatus.PENDING_VERIFICATION;
        this.loyaltyPoints = loyaltyPoints != null ? loyaltyPoints : 0;
        this.accountCredit = accountCredit != null ? accountCredit : BigDecimal.ZERO;
        this.emailNotifications = emailNotifications != null ? emailNotifications : true;
        this.smsNotifications = smsNotifications != null ? smsNotifications : true;
        this.pushNotifications = pushNotifications != null ? pushNotifications : true;
        this.marketingEmails = marketingEmails != null ? marketingEmails : false;
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        this.preferences = preferences;
        this.totalOrders = totalOrders != null ? totalOrders : 0;
        this.totalSpent = totalSpent != null ? totalSpent : BigDecimal.ZERO;
        this.lastOrderDate = lastOrderDate;
        this.emailVerified = emailVerified != null ? emailVerified : false;
        this.phoneVerified = phoneVerified != null ? phoneVerified : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCognitoSub() {
        return cognitoSub;
    }

    public void setCognitoSub(String cognitoSub) {
        this.cognitoSub = cognitoSub;
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

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public CustomerPreference getPreferences() {
        return preferences;
    }

    public void setPreferences(CustomerPreference preferences) {
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Helper methods
    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCustomer(null);
    }

    public void incrementLoyaltyPoints(int points) {
        this.loyaltyPoints = (this.loyaltyPoints == null ? 0 : this.loyaltyPoints) + points;
    }

    public void addCredit(BigDecimal amount) {
        this.accountCredit = (this.accountCredit == null ? BigDecimal.ZERO : this.accountCredit).add(amount);
    }

    public void deductCredit(BigDecimal amount) {
        this.accountCredit = (this.accountCredit == null ? BigDecimal.ZERO : this.accountCredit).subtract(amount);
    }

    public void incrementOrderStats(BigDecimal orderAmount) {
        this.totalOrders = (this.totalOrders == null ? 0 : this.totalOrders) + 1;
        this.totalSpent = (this.totalSpent == null ? BigDecimal.ZERO : this.totalSpent).add(orderAmount);
        this.lastOrderDate = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return CustomerStatus.ACTIVE.equals(this.status);
    }

    public boolean isVerified() {
        return Boolean.TRUE.equals(this.emailVerified);
    }

    // Builder
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public static class CustomerBuilder {
        private Long id;
        private String cognitoSub;
        private String email;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String gender;
        private String profileImageUrl;
        private CustomerStatus status = CustomerStatus.PENDING_VERIFICATION;
        private Integer loyaltyPoints = 0;
        private BigDecimal accountCredit = BigDecimal.ZERO;
        private Boolean emailNotifications = true;
        private Boolean smsNotifications = true;
        private Boolean pushNotifications = true;
        private Boolean marketingEmails = false;
        private List<Address> addresses = new ArrayList<>();
        private CustomerPreference preferences;
        private Integer totalOrders = 0;
        private BigDecimal totalSpent = BigDecimal.ZERO;
        private LocalDateTime lastOrderDate;
        private Boolean emailVerified = false;
        private Boolean phoneVerified = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        CustomerBuilder() {
        }

        public CustomerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CustomerBuilder cognitoSub(String cognitoSub) {
            this.cognitoSub = cognitoSub;
            return this;
        }

        public CustomerBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public CustomerBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public CustomerBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CustomerBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public CustomerBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public CustomerBuilder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public CustomerBuilder status(CustomerStatus status) {
            this.status = status;
            return this;
        }

        public CustomerBuilder loyaltyPoints(Integer loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public CustomerBuilder accountCredit(BigDecimal accountCredit) {
            this.accountCredit = accountCredit;
            return this;
        }

        public CustomerBuilder emailNotifications(Boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
            return this;
        }

        public CustomerBuilder smsNotifications(Boolean smsNotifications) {
            this.smsNotifications = smsNotifications;
            return this;
        }

        public CustomerBuilder pushNotifications(Boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
            return this;
        }

        public CustomerBuilder marketingEmails(Boolean marketingEmails) {
            this.marketingEmails = marketingEmails;
            return this;
        }

        public CustomerBuilder addresses(List<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        public CustomerBuilder preferences(CustomerPreference preferences) {
            this.preferences = preferences;
            return this;
        }

        public CustomerBuilder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public CustomerBuilder totalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public CustomerBuilder lastOrderDate(LocalDateTime lastOrderDate) {
            this.lastOrderDate = lastOrderDate;
            return this;
        }

        public CustomerBuilder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public CustomerBuilder phoneVerified(Boolean phoneVerified) {
            this.phoneVerified = phoneVerified;
            return this;
        }

        public CustomerBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CustomerBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CustomerBuilder deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public Customer build() {
            return new Customer(id, cognitoSub, email, phoneNumber, firstName, lastName, dateOfBirth,
                              gender, profileImageUrl, status, loyaltyPoints, accountCredit,
                              emailNotifications, smsNotifications, pushNotifications, marketingEmails,
                              addresses, preferences, totalOrders, totalSpent, lastOrderDate,
                              emailVerified, phoneVerified, createdAt, updatedAt, deletedAt);
        }
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
               Objects.equals(cognitoSub, customer.cognitoSub) &&
               Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cognitoSub, email);
    }

    // toString
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", cognitoSub='" + cognitoSub + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                '}';
    }
}

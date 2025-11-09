package com.cookedspecially.customerservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private CustomerStatus status = CustomerStatus.PENDING_VERIFICATION;

    // Loyalty and credits
    @Column(name = "loyalty_points")
    @Builder.Default
    private Integer loyaltyPoints = 0;

    @Column(name = "account_credit", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal accountCredit = BigDecimal.ZERO;

    // Preferences
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = true;

    @Column(name = "push_notifications")
    @Builder.Default
    private Boolean pushNotifications = true;

    @Column(name = "marketing_emails")
    @Builder.Default
    private Boolean marketingEmails = false;

    // Addresses
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    // Preferences
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private CustomerPreference preferences;

    // Statistics
    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    // Verification
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
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
}

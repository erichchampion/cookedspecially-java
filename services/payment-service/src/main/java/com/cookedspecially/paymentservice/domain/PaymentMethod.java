package com.cookedspecially.paymentservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Payment Method Entity
 *
 * Represents a stored payment method for a customer
 */
@Entity
@Table(name = "payment_methods", indexes = {
    @Index(name = "idx_customer_id", columnList = "customerId"),
    @Index(name = "idx_external_id", columnList = "externalPaymentMethodId")
})
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethodType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    @Column(length = 255)
    private String externalPaymentMethodId;

    @Column(length = 100)
    private String cardBrand;

    @Column(length = 4)
    private String cardLast4;

    @Column(length = 2)
    private String cardExpMonth;

    @Column(length = 4)
    private String cardExpYear;

    @Column(length = 255)
    private String billingName;

    @Column(length = 500)
    private String billingAddress;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public PaymentMethod() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public PaymentMethodType getType() {
        return type;
    }

    public void setType(PaymentMethodType type) {
        this.type = type;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public String getExternalPaymentMethodId() {
        return externalPaymentMethodId;
    }

    public void setExternalPaymentMethodId(String externalPaymentMethodId) {
        this.externalPaymentMethodId = externalPaymentMethodId;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public void setCardExpMonth(String cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public void setCardExpYear(String cardExpYear) {
        this.cardExpYear = cardExpYear;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    /**
     * Get masked card number for display
     */
    public String getMaskedCardNumber() {
        if (cardLast4 != null) {
            return "**** **** **** " + cardLast4;
        }
        return null;
    }

    /**
     * Check if card is expired
     */
    public boolean isExpired() {
        if (cardExpMonth == null || cardExpYear == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int expYear = Integer.parseInt(cardExpYear);
        int expMonth = Integer.parseInt(cardExpMonth);

        return expYear < currentYear || (expYear == currentYear && expMonth < currentMonth);
    }
}

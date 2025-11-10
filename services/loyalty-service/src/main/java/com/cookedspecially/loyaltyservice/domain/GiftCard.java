package com.cookedspecially.loyaltyservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Gift Card entity
 */
@Entity
@Table(name = "gift_cards", indexes = {
    @Index(name = "idx_card_number", columnList = "cardNumber"),
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_purchaser_customer_id", columnList = "purchaserCustomerId")
})
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cardNumber;

    @Column(nullable = false)
    private Integer restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GiftCardStatus status = GiftCardStatus.CREATED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal initialAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentBalance;

    @Column(length = 100)
    private String category; // e.g., "Birthday", "Holiday", "Thank You"

    // Purchaser information
    @Column
    private Integer purchaserCustomerId;

    @Column(length = 20)
    private String purchaserPhone;

    @Column(length = 100)
    private String purchaserEmail;

    // Recipient information
    @Column(length = 100)
    private String recipientName;

    @Column(length = 20)
    private String recipientPhone;

    @Column(length = 100)
    private String recipientEmail;

    @Column(length = 500)
    private String message;

    // Activation
    @Column
    private LocalDateTime activatedAt;

    @Column
    private Integer activatedBy; // user ID

    // Expiration
    @Column
    private LocalDateTime expiresAt;

    // Purchase details
    @Column
    private String invoiceId;

    @Column
    private LocalDateTime purchasedAt;

    // Redemption
    @Column
    private LocalDateTime redeemedAt;

    @Column
    private Integer redeemedByCustomerId;

    @Column
    private Integer redeemedByUserId;

    // Audit fields
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Integer createdBy; // user ID

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public GiftCardStatus getStatus() {
        return status;
    }

    public void setStatus(GiftCardStatus status) {
        this.status = status;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPurchaserCustomerId() {
        return purchaserCustomerId;
    }

    public void setPurchaserCustomerId(Integer purchaserCustomerId) {
        this.purchaserCustomerId = purchaserCustomerId;
    }

    public String getPurchaserPhone() {
        return purchaserPhone;
    }

    public void setPurchaserPhone(String purchaserPhone) {
        this.purchaserPhone = purchaserPhone;
    }

    public String getPurchaserEmail() {
        return purchaserEmail;
    }

    public void setPurchaserEmail(String purchaserEmail) {
        this.purchaserEmail = purchaserEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Integer getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(Integer activatedBy) {
        this.activatedBy = activatedBy;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public Integer getRedeemedByCustomerId() {
        return redeemedByCustomerId;
    }

    public void setRedeemedByCustomerId(Integer redeemedByCustomerId) {
        this.redeemedByCustomerId = redeemedByCustomerId;
    }

    public Integer getRedeemedByUserId() {
        return redeemedByUserId;
    }

    public void setRedeemedByUserId(Integer redeemedByUserId) {
        this.redeemedByUserId = redeemedByUserId;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    // Business methods
    public boolean isActive() {
        if (status != GiftCardStatus.ACTIVE) {
            return false;
        }
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        return true;
    }

    public boolean canRedeem(BigDecimal amount) {
        return isActive() && currentBalance.compareTo(amount) >= 0;
    }

    public void deductBalance(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        if (this.currentBalance.compareTo(BigDecimal.ZERO) == 0) {
            this.status = GiftCardStatus.REDEEMED;
            this.redeemedAt = LocalDateTime.now();
        }
    }

    public void activate() {
        this.status = GiftCardStatus.ACTIVE;
        this.activatedAt = LocalDateTime.now();
    }

    public String getFormattedCardNumber() {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        return String.format("%s-%s-%s-%s",
                cardNumber.substring(0, 4),
                cardNumber.substring(4, 8),
                cardNumber.substring(8, 12),
                cardNumber.substring(12, 16));
    }
}

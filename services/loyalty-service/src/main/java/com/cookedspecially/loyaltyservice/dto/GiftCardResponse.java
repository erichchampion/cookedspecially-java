package com.cookedspecially.loyaltyservice.dto;

import com.cookedspecially.loyaltyservice.domain.GiftCard;
import com.cookedspecially.loyaltyservice.domain.GiftCardStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiftCardResponse {
    private Long id;
    private String cardNumber;
    private String formattedCardNumber;
    private Integer restaurantId;
    private GiftCardStatus status;
    private BigDecimal initialAmount;
    private BigDecimal currentBalance;
    private String category;
    private String recipientName;
    private LocalDateTime expiresAt;
    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;
    private Boolean isActive;

    public GiftCardResponse() {}

    public GiftCardResponse(GiftCard giftCard) {
        this.id = giftCard.getId();
        this.cardNumber = giftCard.getCardNumber();
        this.formattedCardNumber = giftCard.getFormattedCardNumber();
        this.restaurantId = giftCard.getRestaurantId();
        this.status = giftCard.getStatus();
        this.initialAmount = giftCard.getInitialAmount();
        this.currentBalance = giftCard.getCurrentBalance();
        this.category = giftCard.getCategory();
        this.recipientName = giftCard.getRecipientName();
        this.expiresAt = giftCard.getExpiresAt();
        this.activatedAt = giftCard.getActivatedAt();
        this.createdAt = giftCard.getCreatedAt();
        this.isActive = giftCard.isActive();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getFormattedCardNumber() { return formattedCardNumber; }
    public void setFormattedCardNumber(String formattedCardNumber) { this.formattedCardNumber = formattedCardNumber; }
    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }
    public GiftCardStatus getStatus() { return status; }
    public void setStatus(GiftCardStatus status) { this.status = status; }
    public BigDecimal getInitialAmount() { return initialAmount; }
    public void setInitialAmount(BigDecimal initialAmount) { this.initialAmount = initialAmount; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}

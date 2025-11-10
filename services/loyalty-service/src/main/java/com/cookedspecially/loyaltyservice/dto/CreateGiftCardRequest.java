package com.cookedspecially.loyaltyservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateGiftCardRequest {
    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 100)
    private String category;

    @Min(value = 1, message = "Expiry days must be at least 1")
    private Integer expiryDays = 365; // Default 1 year

    @Min(value = 1, message = "Count must be at least 1")
    private Integer count = 1; // For bulk creation

    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String message;

    // Getters and Setters
    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getExpiryDays() { return expiryDays; }
    public void setExpiryDays(Integer expiryDays) { this.expiryDays = expiryDays; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

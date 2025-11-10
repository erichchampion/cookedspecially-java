package com.cookedspecially.loyaltyservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RedeemGiftCardRequest {
    @NotBlank(message = "Gift card number is required")
    private String cardNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    private Long orderId;

    // Getters and Setters
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}

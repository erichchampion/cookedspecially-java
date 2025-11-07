package com.cookedspecially.dto.saleRegister;

public class PaymentCategoryDTO {
    public String category;
    public String paymentTypeName;
    public float pendingAmount;
    public float completedAmount;

    public PaymentCategoryDTO() {
    }

    public PaymentCategoryDTO(String category, String paymentTypeName) {
        this.category = category;
        this.paymentTypeName = paymentTypeName;
    }
}

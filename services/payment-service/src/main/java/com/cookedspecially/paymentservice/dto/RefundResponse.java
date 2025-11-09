package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.Refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Response DTO
 */
public class RefundResponse {

    private Long id;
    private String refundNumber;
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private String reason;
    private Refund.RefundStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Constructors
    public RefundResponse() {
    }

    public RefundResponse(Long id,
                 String refundNumber,
                 Long paymentId,
                 Long orderId,
                 BigDecimal amount,
                 String reason,
                 Refund.RefundStatus status,
                 String failureReason,
                 LocalDateTime createdAt,
                 LocalDateTime completedAt) {
        this.id = id;
        this.refundNumber = refundNumber;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getRefundNumber() {
        return refundNumber;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public Refund.RefundStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setRefundNumber(String refundNumber) {
        this.refundNumber = refundNumber;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(Refund.RefundStatus status) {
        this.status = status;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }


    public static RefundResponse fromEntity(Refund refund) {
        RefundResponse response = new RefundResponse();
        response.setId(refund.getId());
        response.setRefundNumber(refund.getRefundNumber());
        response.setPaymentId(refund.getPaymentId());
        response.setOrderId(refund.getOrderId());
        response.setAmount(refund.getAmount());
        response.setReason(refund.getReason());
        response.setStatus(refund.getStatus());
        response.setFailureReason(refund.getFailureReason());
        response.setCreatedAt(refund.getCreatedAt());
        response.setCompletedAt(refund.getCompletedAt());
        return response;
    }
}

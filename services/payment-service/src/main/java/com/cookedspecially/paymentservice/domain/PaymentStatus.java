package com.cookedspecially.paymentservice.domain;

/**
 * Payment Status Enum
 *
 * Represents the various states a payment can be in
 */
public enum PaymentStatus {
    /**
     * Payment initiated but not yet processed
     */
    PENDING,

    /**
     * Payment is being processed by payment gateway
     */
    PROCESSING,

    /**
     * Payment requires additional action (e.g., 3D Secure)
     */
    REQUIRES_ACTION,

    /**
     * Payment successfully completed
     */
    COMPLETED,

    /**
     * Payment failed
     */
    FAILED,

    /**
     * Payment cancelled by user or system
     */
    CANCELLED,

    /**
     * Payment refunded (partial or full)
     */
    REFUNDED,

    /**
     * Payment disputed/chargeback initiated
     */
    DISPUTED;

    /**
     * Check if payment is in a final state (cannot be changed)
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == COMPLETED;
    }
}

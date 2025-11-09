package com.cookedspecially.paymentservice.exception;

/**
 * Exception thrown when refund processing fails
 */
public class RefundException extends RuntimeException {

    private final Long paymentId;

    public RefundException(String message) {
        super(message);
        this.paymentId = null;
    }

    public RefundException(Long paymentId, String message) {
        super(message);
        this.paymentId = paymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }
}

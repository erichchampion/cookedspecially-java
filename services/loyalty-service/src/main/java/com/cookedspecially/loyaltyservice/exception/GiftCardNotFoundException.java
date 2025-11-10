package com.cookedspecially.loyaltyservice.exception;

public class GiftCardNotFoundException extends RuntimeException {
    public GiftCardNotFoundException(String message) {
        super(message);
    }
}

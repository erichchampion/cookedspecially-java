package com.cookedspecially.kitchenservice.domain;

/**
 * Type of till transaction
 */
public enum TransactionType {
    SALE,              // Sale transaction (order payment)
    REFUND,            // Refund transaction
    CASH_IN,           // Cash added to till
    CASH_OUT,          // Cash withdrawn from till
    OPENING_BALANCE,   // Opening balance for shift
    CLOSING_BALANCE,   // Closing balance for shift
    ADJUSTMENT         // Manual adjustment
}

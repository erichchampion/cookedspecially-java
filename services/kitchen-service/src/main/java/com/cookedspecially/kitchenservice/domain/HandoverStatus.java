package com.cookedspecially.kitchenservice.domain;

/**
 * Status of till handover between shifts
 */
public enum HandoverStatus {
    PENDING,      // Handover initiated, awaiting approval
    APPROVED,     // Handover approved
    REJECTED,     // Handover rejected due to discrepancies
    COMPLETED     // Handover completed successfully
}

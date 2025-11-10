package com.cookedspecially.integrationhubservice.domain;

public enum WebhookStatus {
    RECEIVED,
    PROCESSING,
    COMPLETED,
    FAILED,
    RETRY_SCHEDULED,
    DEAD_LETTER
}

package com.cookedspecially.orderservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates unique order numbers
 * Format: ORD-YYYYMMDD-XXXXXX
 * Example: ORD-20250107-A3B5C7
 */
@Component
public class OrderNumberGenerator {

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generate a unique order number
     *
     * @return Order number in format ORD-YYYYMMDD-XXXXXX
     */
    public String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = generateRandomString();
        return String.format("%s-%s-%s", PREFIX, datePart, randomPart);
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}

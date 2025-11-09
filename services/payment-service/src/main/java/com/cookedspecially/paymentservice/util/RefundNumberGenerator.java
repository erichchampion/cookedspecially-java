package com.cookedspecially.paymentservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates unique refund numbers
 * Format: REF-YYYYMMDD-XXXXXX
 * Example: REF-20250107-C8E3G1
 */
@Component
public class RefundNumberGenerator {

    private static final String PREFIX = "REF";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

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

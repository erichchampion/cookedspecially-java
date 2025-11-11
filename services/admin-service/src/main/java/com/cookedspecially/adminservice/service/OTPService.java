package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.OTP;
import com.cookedspecially.adminservice.domain.OTPType;
import com.cookedspecially.adminservice.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {

    private final OTPRepository otpRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.otp.length:6}")
    private int otpLength;

    @Value("${auth.otp.expiry-minutes:5}")
    private int expiryMinutes;

    @Value("${auth.otp.max-attempts:3}")
    private int maxAttempts;

    @Transactional
    public String generateOTP(Long userId, OTPType type) {
        log.info("Generating OTP for user: userId={}, type={}", userId, type);

        String code = generateRandomCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);

        OTP otp = OTP.builder()
                .userId(userId)
                .code(code)
                .type(type)
                .expiresAt(expiresAt)
                .used(false)
                .attempts(0)
                .build();

        otpRepository.save(otp);
        log.info("OTP generated successfully for user: userId={}", userId);

        return code;
    }

    @Transactional
    public boolean validateOTP(Long userId, String code, OTPType type) {
        log.info("Validating OTP for user: userId={}, type={}", userId, type);

        OTP otp = otpRepository.findTopByUserIdAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                userId, type, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("No valid OTP found"));

        if (otp.getAttempts() >= maxAttempts) {
            throw new RuntimeException("Max OTP attempts exceeded");
        }

        otp.setAttempts(otp.getAttempts() + 1);

        if (!otp.getCode().equals(code)) {
            otpRepository.save(otp);
            log.warn("Invalid OTP code for user: userId={}", userId);
            return false;
        }

        otp.setUsed(true);
        otpRepository.save(otp);
        log.info("OTP validated successfully for user: userId={}", userId);

        return true;
    }

    @Transactional
    public void cleanupExpiredOTPs() {
        log.info("Cleaning up expired OTPs");
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }
}

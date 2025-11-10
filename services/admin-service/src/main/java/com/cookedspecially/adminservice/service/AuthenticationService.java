package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.OTPType;
import com.cookedspecially.adminservice.domain.User;
import com.cookedspecially.adminservice.dto.auth.LoginRequestDTO;
import com.cookedspecially.adminservice.dto.auth.LoginResponseDTO;
import com.cookedspecially.adminservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;
    private final SessionManagementService sessionManagementService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, String ipAddress, String userAgent) {
        log.info("Login attempt: usernameOrEmail={}", request.getUsernameOrEmail());

        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is locked");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }

        resetFailedLoginAttempts(user);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        if (user.getMfaEnabled()) {
            String otpCode = otpService.generateOTP(user.getId(), OTPType.MFA);
            log.info("MFA required for user: userId={}", user.getId());

            return LoginResponseDTO.builder()
                    .mfaRequired(true)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        String sessionToken = sessionManagementService.createSession(user.getId(), ipAddress, userAgent,
                request.getRememberMe() != null && request.getRememberMe() ? 30 : 1);

        log.info("Login successful: userId={}", user.getId());

        return LoginResponseDTO.builder()
                .accessToken(sessionToken)
                .tokenType("Bearer")
                .expiresIn(request.getRememberMe() != null && request.getRememberMe() ? 30 * 24 * 60 * 60 : 24 * 60 * 60)
                .sessionId(sessionToken)
                .mfaRequired(false)
                .userId(user.getId())
                .username(user.getUsername())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void logout(String sessionToken) {
        log.info("Logout: sessionToken={}", sessionToken);
        sessionManagementService.invalidateSession(sessionToken);
    }

    @Transactional
    public void resetPassword(Long userId, String otpCode, String newPassword) {
        log.info("Password reset for user: userId={}", userId);

        if (!otpService.validateOTP(userId, otpCode, OTPType.PASSWORD_RESET)) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password reset successful: userId={}", userId);
    }

    private void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusHours(1));
            log.warn("Account locked due to failed login attempts: userId={}", user.getId());
        }
        userRepository.save(user);
    }

    private void resetFailedLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }
}

package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.UserSession;
import com.cookedspecially.adminservice.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final UserSessionRepository sessionRepository;

    @Transactional
    public String createSession(Long userId, String ipAddress, String userAgent, int expiryDays) {
        log.info("Creating session for user: userId={}", userId);

        String sessionToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);

        UserSession session = UserSession.builder()
                .userId(userId)
                .sessionToken(sessionToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .active(true)
                .build();

        sessionRepository.save(session);
        log.info("Session created: sessionToken={}", sessionToken);

        return sessionToken;
    }

    @Transactional
    public void invalidateSession(String sessionToken) {
        log.info("Invalidating session: sessionToken={}", sessionToken);

        sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void invalidateAllUserSessions(Long userId) {
        log.info("Invalidating all sessions for user: userId={}", userId);

        List<UserSession> sessions = sessionRepository.findByUserIdAndActiveTrue(userId);
        sessions.forEach(session -> session.setActive(false));
        sessionRepository.saveAll(sessions);
    }

    @Transactional
    public void cleanupExpiredSessions() {
        log.info("Cleaning up expired sessions");
        sessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public boolean isSessionValid(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken)
                .map(session -> session.getActive() && session.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}

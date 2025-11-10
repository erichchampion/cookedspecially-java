package com.cookedspecially.adminservice.repository;

import com.cookedspecially.adminservice.domain.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(String sessionToken);
    List<UserSession> findByUserIdAndActiveTrue(Long userId);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}

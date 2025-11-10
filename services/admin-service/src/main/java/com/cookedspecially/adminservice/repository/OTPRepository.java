package com.cookedspecially.adminservice.repository;

import com.cookedspecially.adminservice.domain.OTP;
import com.cookedspecially.adminservice.domain.OTPType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findTopByUserIdAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
        Long userId, OTPType type, LocalDateTime now);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}

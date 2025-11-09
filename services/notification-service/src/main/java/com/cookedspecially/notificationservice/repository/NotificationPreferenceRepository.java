package com.cookedspecially.notificationservice.repository;

import com.cookedspecially.notificationservice.domain.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Notification Preference Repository
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    /**
     * Find preferences by user ID
     */
    Optional<NotificationPreference> findByUserId(Long userId);

    /**
     * Check if preferences exist for user
     */
    boolean existsByUserId(Long userId);

    /**
     * Delete preferences by user ID
     */
    void deleteByUserId(Long userId);
}

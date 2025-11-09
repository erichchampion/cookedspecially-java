package com.cookedspecially.notificationservice.repository;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationTemplate;
import com.cookedspecially.notificationservice.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification Template Repository
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    /**
     * Find template by key
     */
    Optional<NotificationTemplate> findByTemplateKey(String templateKey);

    /**
     * Find template by type and channel
     */
    Optional<NotificationTemplate> findByTypeAndChannelAndIsActiveTrue(
        NotificationType type,
        NotificationChannel channel
    );

    /**
     * Find templates by type
     */
    List<NotificationTemplate> findByTypeAndIsActiveTrue(NotificationType type);

    /**
     * Find templates by channel
     */
    List<NotificationTemplate> findByChannelAndIsActiveTrue(NotificationChannel channel);

    /**
     * Find all active templates
     */
    List<NotificationTemplate> findByIsActiveTrue();

    /**
     * Check if template exists by key
     */
    boolean existsByTemplateKey(String templateKey);
}

package com.cookedspecially.notificationservice.repository;

import com.cookedspecially.notificationservice.domain.Notification;
import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationStatus;
import com.cookedspecially.notificationservice.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by user ID
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    /**
     * Find notifications by user ID and status
     */
    Page<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status, Pageable pageable);

    /**
     * Find notifications by user ID and type
     */
    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);

    /**
     * Find notifications by status
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find failed notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status " +
           "AND (n.retryCount IS NULL OR n.retryCount < n.maxRetries) " +
           "AND n.createdAt > :cutoffTime")
    List<Notification> findRetryableFailedNotifications(
        @Param("status") NotificationStatus status,
        @Param("cutoffTime") LocalDateTime cutoffTime
    );

    /**
     * Find pending notifications by priority
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status " +
           "ORDER BY n.priority DESC, n.createdAt ASC")
    List<Notification> findPendingNotificationsByPriority(
        @Param("status") NotificationStatus status
    );

    /**
     * Count notifications by user and channel in time window
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.channel = :channel AND n.createdAt > :since")
    long countByUserIdAndChannelSince(
        @Param("userId") Long userId,
        @Param("channel") NotificationChannel channel,
        @Param("since") LocalDateTime since
    );

    /**
     * Find notifications by related entity
     */
    Page<Notification> findByRelatedEntityTypeAndRelatedEntityId(
        String relatedEntityType,
        Long relatedEntityId,
        Pageable pageable
    );

    /**
     * Count by user ID and status
     */
    long countByUserIdAndStatus(Long userId, NotificationStatus status);

    /**
     * Delete old notifications
     */
    void deleteByCreatedAtBefore(LocalDateTime cutoffTime);
}

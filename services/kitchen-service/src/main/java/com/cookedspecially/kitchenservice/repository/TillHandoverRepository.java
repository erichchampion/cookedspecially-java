package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.HandoverStatus;
import com.cookedspecially.kitchenservice.domain.TillHandover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Till Handover entity
 */
@Repository
public interface TillHandoverRepository extends JpaRepository<TillHandover, Long> {

    /**
     * Find handovers by till
     */
    List<TillHandover> findByTillIdOrderByHandoverDateDesc(Long tillId);

    /**
     * Find handovers by status
     */
    List<TillHandover> findByStatus(HandoverStatus status);

    /**
     * Find pending handovers by till
     */
    Optional<TillHandover> findByTillIdAndStatus(Long tillId, HandoverStatus status);

    /**
     * Find handovers by user (from or to)
     */
    @Query("SELECT h FROM TillHandover h WHERE h.fromUserId = :userId OR h.toUserId = :userId " +
           "ORDER BY h.handoverDate DESC")
    List<TillHandover> findByUserId(@Param("userId") String userId);

    /**
     * Find handovers in date range
     */
    List<TillHandover> findByHandoverDateBetweenOrderByHandoverDateDesc(
        LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find handovers by till and date range
     */
    List<TillHandover> findByTillIdAndHandoverDateBetweenOrderByHandoverDateDesc(
        Long tillId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count pending handovers for till
     */
    long countByTillIdAndStatus(Long tillId, HandoverStatus status);

    /**
     * Find approved handovers
     */
    List<TillHandover> findByStatusAndApprovedAtBetween(
        HandoverStatus status, LocalDateTime startDate, LocalDateTime endDate);
}

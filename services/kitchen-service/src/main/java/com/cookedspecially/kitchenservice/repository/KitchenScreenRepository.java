package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.KitchenScreen;
import com.cookedspecially.kitchenservice.domain.KitchenScreenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Kitchen Screen entity
 */
@Repository
public interface KitchenScreenRepository extends JpaRepository<KitchenScreen, Long> {

    /**
     * Find screens by fulfillment center
     */
    List<KitchenScreen> findByFulfillmentCenterIdOrderByDisplayOrderAsc(Long fulfillmentCenterId);

    /**
     * Find screens by restaurant
     */
    List<KitchenScreen> findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);

    /**
     * Find screens by status
     */
    List<KitchenScreen> findByStatus(KitchenScreenStatus status);

    /**
     * Find screens by fulfillment center and status
     */
    List<KitchenScreen> findByFulfillmentCenterIdAndStatus(Long fulfillmentCenterId, KitchenScreenStatus status);

    /**
     * Find screen by device ID
     */
    Optional<KitchenScreen> findByDeviceId(String deviceId);

    /**
     * Find screens by station type
     */
    List<KitchenScreen> findByStationType(String stationType);

    /**
     * Find active screens by fulfillment center
     */
    @Query("SELECT s FROM KitchenScreen s WHERE s.fulfillmentCenterId = :fcId " +
           "AND s.status = 'ACTIVE' ORDER BY s.displayOrder ASC")
    List<KitchenScreen> findActiveScreensByFulfillmentCenter(@Param("fcId") Long fulfillmentCenterId);

    /**
     * Find screens with stale heartbeat (offline)
     */
    @Query("SELECT s FROM KitchenScreen s WHERE s.lastHeartbeat < :threshold " +
           "AND s.status = 'ACTIVE'")
    List<KitchenScreen> findScreensWithStaleHeartbeat(@Param("threshold") LocalDateTime threshold);

    /**
     * Find screen by IP address
     */
    Optional<KitchenScreen> findByIpAddress(String ipAddress);

    /**
     * Check if screen exists with name for fulfillment center
     */
    boolean existsByNameAndFulfillmentCenterId(String name, Long fulfillmentCenterId);
}

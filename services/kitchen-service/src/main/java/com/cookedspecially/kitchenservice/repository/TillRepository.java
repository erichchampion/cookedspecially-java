package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.Till;
import com.cookedspecially.kitchenservice.domain.TillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Till entity
 */
@Repository
public interface TillRepository extends JpaRepository<Till, Long> {

    /**
     * Find tills by fulfillment center
     */
    List<Till> findByFulfillmentCenterId(Long fulfillmentCenterId);

    /**
     * Find tills by restaurant
     */
    List<Till> findByRestaurantId(Long restaurantId);

    /**
     * Find tills by status
     */
    List<Till> findByStatus(TillStatus status);

    /**
     * Find tills by fulfillment center and status
     */
    List<Till> findByFulfillmentCenterIdAndStatus(Long fulfillmentCenterId, TillStatus status);

    /**
     * Find tills currently managed by a user
     */
    Optional<Till> findByCurrentUserIdAndStatus(String currentUserId, TillStatus status);

    /**
     * Find open tills by fulfillment center
     */
    @Query("SELECT t FROM Till t WHERE t.fulfillmentCenterId = :fcId AND t.status = 'OPEN'")
    List<Till> findOpenTillsByFulfillmentCenter(@Param("fcId") Long fulfillmentCenterId);

    /**
     * Find till by name and restaurant
     */
    Optional<Till> findByNameAndRestaurantId(String name, Long restaurantId);

    /**
     * Check if till exists with name for restaurant
     */
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);

    /**
     * Find all active tills (OPEN or SUSPENDED) by restaurant
     */
    @Query("SELECT t FROM Till t WHERE t.restaurantId = :restaurantId AND t.status IN ('OPEN', 'SUSPENDED')")
    List<Till> findActiveTillsByRestaurant(@Param("restaurantId") Long restaurantId);
}

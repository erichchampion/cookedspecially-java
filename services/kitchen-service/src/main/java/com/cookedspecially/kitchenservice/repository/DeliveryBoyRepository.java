package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.DeliveryBoy;
import com.cookedspecially.kitchenservice.domain.DeliveryBoyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Delivery Boy entity
 */
@Repository
public interface DeliveryBoyRepository extends JpaRepository<DeliveryBoy, Long> {

    /**
     * Find delivery personnel by restaurant
     */
    List<DeliveryBoy> findByRestaurantIdAndDeletedAtIsNull(Long restaurantId);

    /**
     * Find delivery personnel by status
     */
    List<DeliveryBoy> findByStatusAndDeletedAtIsNull(DeliveryBoyStatus status);

    /**
     * Find delivery personnel by restaurant and status
     */
    List<DeliveryBoy> findByRestaurantIdAndStatusAndDeletedAtIsNull(
        Long restaurantId, DeliveryBoyStatus status);

    /**
     * Find available delivery personnel by restaurant
     */
    @Query("SELECT d FROM DeliveryBoy d WHERE d.restaurantId = :restaurantId " +
           "AND d.status = 'AVAILABLE' AND d.active = true AND d.deletedAt IS NULL " +
           "ORDER BY d.currentDeliveryCount ASC, d.totalDeliveriesCompleted DESC")
    List<DeliveryBoy> findAvailableDeliveryBoys(@Param("restaurantId") Long restaurantId);

    /**
     * Find delivery boy by phone
     */
    Optional<DeliveryBoy> findByPhoneAndDeletedAtIsNull(String phone);

    /**
     * Find active delivery personnel by restaurant
     */
    @Query("SELECT d FROM DeliveryBoy d WHERE d.restaurantId = :restaurantId " +
           "AND d.active = true AND d.deletedAt IS NULL")
    List<DeliveryBoy> findActiveDeliveryBoys(@Param("restaurantId") Long restaurantId);

    /**
     * Find top rated delivery personnel
     */
    @Query("SELECT d FROM DeliveryBoy d WHERE d.restaurantId = :restaurantId " +
           "AND d.active = true AND d.deletedAt IS NULL " +
           "ORDER BY d.averageRating DESC, d.totalDeliveriesCompleted DESC")
    List<DeliveryBoy> findTopRatedDeliveryBoys(@Param("restaurantId") Long restaurantId);

    /**
     * Count active delivery personnel by restaurant
     */
    long countByRestaurantIdAndActiveAndDeletedAtIsNull(Long restaurantId, Boolean active);

    /**
     * Count available delivery personnel by restaurant
     */
    long countByRestaurantIdAndStatusAndActiveAndDeletedAtIsNull(
        Long restaurantId, DeliveryBoyStatus status, Boolean active);

    /**
     * Check if phone exists
     */
    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    /**
     * Find by phone including deleted
     */
    Optional<DeliveryBoy> findByPhone(String phone);
}

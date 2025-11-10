package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.SeatingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Seating Table entity
 */
@Repository
public interface SeatingTableRepository extends JpaRepository<SeatingTable, Long> {

    /**
     * Find tables by restaurant
     */
    List<SeatingTable> findByRestaurantIdAndDeletedAtIsNull(Long restaurantId);

    /**
     * Find tables by fulfillment center
     */
    List<SeatingTable> findByFulfillmentCenterIdAndDeletedAtIsNull(Long fulfillmentCenterId);

    /**
     * Find tables by status
     */
    List<SeatingTable> findByStatusAndDeletedAtIsNull(String status);

    /**
     * Find tables by fulfillment center and status
     */
    List<SeatingTable> findByFulfillmentCenterIdAndStatusAndDeletedAtIsNull(
        Long fulfillmentCenterId, String status);

    /**
     * Find table by table number
     */
    Optional<SeatingTable> findByRestaurantIdAndTableNumberAndDeletedAtIsNull(
        Long restaurantId, String tableNumber);

    /**
     * Find tables by section
     */
    List<SeatingTable> findByFulfillmentCenterIdAndSectionAndDeletedAtIsNull(
        Long fulfillmentCenterId, String section);

    /**
     * Find table by QR code
     */
    Optional<SeatingTable> findByQrCodeAndDeletedAtIsNull(String qrCode);

    /**
     * Find table by current order
     */
    Optional<SeatingTable> findByCurrentOrderIdAndDeletedAtIsNull(Long currentOrderId);

    /**
     * Find available tables by fulfillment center
     */
    @Query("SELECT t FROM SeatingTable t WHERE t.fulfillmentCenterId = :fcId " +
           "AND t.status = 'AVAILABLE' AND t.active = true AND t.deletedAt IS NULL " +
           "ORDER BY t.tableNumber ASC")
    List<SeatingTable> findAvailableTables(@Param("fcId") Long fulfillmentCenterId);

    /**
     * Find occupied tables by fulfillment center
     */
    @Query("SELECT t FROM SeatingTable t WHERE t.fulfillmentCenterId = :fcId " +
           "AND t.status = 'OCCUPIED' AND t.deletedAt IS NULL " +
           "ORDER BY t.occupiedSince ASC")
    List<SeatingTable> findOccupiedTables(@Param("fcId") Long fulfillmentCenterId);

    /**
     * Count tables by status and fulfillment center
     */
    long countByFulfillmentCenterIdAndStatusAndDeletedAtIsNull(Long fulfillmentCenterId, String status);

    /**
     * Check if table number exists for restaurant
     */
    boolean existsByRestaurantIdAndTableNumberAndDeletedAtIsNull(Long restaurantId, String tableNumber);

    /**
     * Find active tables by fulfillment center
     */
    @Query("SELECT t FROM SeatingTable t WHERE t.fulfillmentCenterId = :fcId " +
           "AND t.active = true AND t.deletedAt IS NULL ORDER BY t.tableNumber ASC")
    List<SeatingTable> findActiveTablesByFulfillmentCenter(@Param("fcId") Long fulfillmentCenterId);
}

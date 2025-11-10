package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.DeliveryArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Delivery Area entity
 */
@Repository
public interface DeliveryAreaRepository extends JpaRepository<DeliveryArea, Long> {

    /**
     * Find delivery areas by restaurant
     */
    List<DeliveryArea> findByRestaurantIdAndDeletedAtIsNullOrderByDisplayOrderAsc(Long restaurantId);

    /**
     * Find active delivery areas by restaurant
     */
    @Query("SELECT d FROM DeliveryArea d WHERE d.restaurantId = :restaurantId " +
           "AND d.active = true AND d.deletedAt IS NULL ORDER BY d.displayOrder ASC")
    List<DeliveryArea> findActiveDeliveryAreas(@Param("restaurantId") Long restaurantId);

    /**
     * Find delivery area by zip code
     */
    List<DeliveryArea> findByRestaurantIdAndZipCodeAndDeletedAtIsNull(Long restaurantId, String zipCode);

    /**
     * Find delivery area by name
     */
    Optional<DeliveryArea> findByRestaurantIdAndNameAndDeletedAtIsNull(Long restaurantId, String name);

    /**
     * Find delivery areas by city
     */
    List<DeliveryArea> findByRestaurantIdAndCityAndDeletedAtIsNull(Long restaurantId, String city);

    /**
     * Check if area exists with name for restaurant
     */
    boolean existsByRestaurantIdAndNameAndDeletedAtIsNull(Long restaurantId, String name);

    /**
     * Count active delivery areas by restaurant
     */
    long countByRestaurantIdAndActiveAndDeletedAtIsNull(Long restaurantId, Boolean active);
}

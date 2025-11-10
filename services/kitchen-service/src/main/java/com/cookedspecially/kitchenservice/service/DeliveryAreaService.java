package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.DeliveryArea;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Delivery Area operations
 */
public interface DeliveryAreaService {

    /**
     * Create a new delivery area
     */
    DeliveryArea createDeliveryArea(DeliveryArea deliveryArea);

    /**
     * Update delivery area
     */
    DeliveryArea updateDeliveryArea(Long areaId, DeliveryArea deliveryArea);

    /**
     * Get delivery area by ID
     */
    DeliveryArea getDeliveryAreaById(Long areaId);

    /**
     * Get all delivery areas for a restaurant
     */
    List<DeliveryArea> getDeliveryAreasByRestaurant(Long restaurantId);

    /**
     * Get active delivery areas for restaurant
     */
    List<DeliveryArea> getActiveDeliveryAreas(Long restaurantId);

    /**
     * Find delivery areas by zip code
     */
    List<DeliveryArea> findDeliveryAreasByZipCode(Long restaurantId, String zipCode);

    /**
     * Find delivery area by city
     */
    List<DeliveryArea> findDeliveryAreasByCity(Long restaurantId, String city);

    /**
     * Calculate delivery charge for order amount
     */
    BigDecimal calculateDeliveryCharge(Long areaId, BigDecimal orderAmount);

    /**
     * Delete delivery area (soft delete)
     */
    void deleteDeliveryArea(Long areaId);

    /**
     * Restore deleted delivery area
     */
    DeliveryArea restoreDeliveryArea(Long areaId);
}

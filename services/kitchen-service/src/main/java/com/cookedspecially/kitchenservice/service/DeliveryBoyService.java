package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.DeliveryBoy;
import com.cookedspecially.kitchenservice.domain.DeliveryBoyStatus;

import java.util.List;

/**
 * Service interface for Delivery Boy operations
 */
public interface DeliveryBoyService {

    /**
     * Create a new delivery boy
     */
    DeliveryBoy createDeliveryBoy(DeliveryBoy deliveryBoy);

    /**
     * Update delivery boy information
     */
    DeliveryBoy updateDeliveryBoy(Long deliveryBoyId, DeliveryBoy deliveryBoy);

    /**
     * Get delivery boy by ID
     */
    DeliveryBoy getDeliveryBoyById(Long deliveryBoyId);

    /**
     * Get all delivery boys for a restaurant
     */
    List<DeliveryBoy> getDeliveryBoysByRestaurant(Long restaurantId);

    /**
     * Get active delivery boys for restaurant
     */
    List<DeliveryBoy> getActiveDeliveryBoys(Long restaurantId);

    /**
     * Get available delivery boys for assignment
     */
    List<DeliveryBoy> getAvailableDeliveryBoys(Long restaurantId);

    /**
     * Get delivery boys by status
     */
    List<DeliveryBoy> getDeliveryBoysByStatus(DeliveryBoyStatus status);

    /**
     * Get top rated delivery boys
     */
    List<DeliveryBoy> getTopRatedDeliveryBoys(Long restaurantId);

    /**
     * Assign delivery to delivery boy
     */
    DeliveryBoy assignDelivery(Long deliveryBoyId);

    /**
     * Complete delivery
     */
    DeliveryBoy completeDelivery(Long deliveryBoyId);

    /**
     * Update delivery boy status
     */
    DeliveryBoy updateStatus(Long deliveryBoyId, DeliveryBoyStatus status);

    /**
     * Update delivery boy rating
     */
    DeliveryBoy updateRating(Long deliveryBoyId, double rating);

    /**
     * Mark delivery boy as available
     */
    DeliveryBoy markAvailable(Long deliveryBoyId);

    /**
     * Mark delivery boy on break
     */
    DeliveryBoy markOnBreak(Long deliveryBoyId);

    /**
     * Mark delivery boy offline
     */
    DeliveryBoy markOffline(Long deliveryBoyId);

    /**
     * Delete delivery boy (soft delete)
     */
    void deleteDeliveryBoy(Long deliveryBoyId);

    /**
     * Restore deleted delivery boy
     */
    DeliveryBoy restoreDeliveryBoy(Long deliveryBoyId);
}

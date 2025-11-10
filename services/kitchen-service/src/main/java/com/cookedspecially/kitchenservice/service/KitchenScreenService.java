package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.KitchenScreen;
import com.cookedspecially.kitchenservice.domain.KitchenScreenStatus;

import java.util.List;

/**
 * Service interface for Kitchen Screen operations
 */
public interface KitchenScreenService {

    /**
     * Create a new kitchen screen
     */
    KitchenScreen createScreen(KitchenScreen screen);

    /**
     * Update kitchen screen
     */
    KitchenScreen updateScreen(Long screenId, KitchenScreen screen);

    /**
     * Get screen by ID
     */
    KitchenScreen getScreenById(Long screenId);

    /**
     * Get all screens for a fulfillment center
     */
    List<KitchenScreen> getScreensByFulfillmentCenter(Long fulfillmentCenterId);

    /**
     * Get active screens for fulfillment center
     */
    List<KitchenScreen> getActiveScreens(Long fulfillmentCenterId);

    /**
     * Get screens by status
     */
    List<KitchenScreen> getScreensByStatus(KitchenScreenStatus status);

    /**
     * Get screen by device ID
     */
    KitchenScreen getScreenByDeviceId(String deviceId);

    /**
     * Update screen heartbeat (mark as online)
     */
    KitchenScreen updateHeartbeat(Long screenId);

    /**
     * Mark screen as offline
     */
    KitchenScreen markOffline(Long screenId);

    /**
     * Mark screen as active
     */
    KitchenScreen markActive(Long screenId);

    /**
     * Mark screen as maintenance
     */
    KitchenScreen markMaintenance(Long screenId);

    /**
     * Check for offline screens and mark them
     */
    void checkAndMarkOfflineScreens();

    /**
     * Delete screen
     */
    void deleteScreen(Long screenId);
}

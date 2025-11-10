package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.SeatingTable;

import java.util.List;

/**
 * Service interface for Seating Table operations
 */
public interface SeatingTableService {

    /**
     * Create a new seating table
     */
    SeatingTable createTable(SeatingTable table);

    /**
     * Update seating table
     */
    SeatingTable updateTable(Long tableId, SeatingTable table);

    /**
     * Get table by ID
     */
    SeatingTable getTableById(Long tableId);

    /**
     * Get tables by fulfillment center
     */
    List<SeatingTable> getTablesByFulfillmentCenter(Long fulfillmentCenterId);

    /**
     * Get tables by status
     */
    List<SeatingTable> getTablesByStatus(String status);

    /**
     * Get available tables
     */
    List<SeatingTable> getAvailableTables(Long fulfillmentCenterId);

    /**
     * Get occupied tables
     */
    List<SeatingTable> getOccupiedTables(Long fulfillmentCenterId);

    /**
     * Find table by table number
     */
    SeatingTable getTableByNumber(Long restaurantId, String tableNumber);

    /**
     * Find table by QR code
     */
    SeatingTable getTableByQrCode(String qrCode);

    /**
     * Find table by current order
     */
    SeatingTable getTableByOrderId(Long orderId);

    /**
     * Occupy table with order
     */
    SeatingTable occupyTable(Long tableId, Long orderId);

    /**
     * Release table (order completed)
     */
    SeatingTable releaseTable(Long tableId);

    /**
     * Reserve table
     */
    SeatingTable reserveTable(Long tableId);

    /**
     * Mark table for cleaning
     */
    SeatingTable markForCleaning(Long tableId);

    /**
     * Mark table as available
     */
    SeatingTable markAvailable(Long tableId);

    /**
     * Generate QR code for table
     */
    SeatingTable generateQrCode(Long tableId);

    /**
     * Delete table (soft delete)
     */
    void deleteTable(Long tableId);

    /**
     * Restore deleted table
     */
    SeatingTable restoreTable(Long tableId);
}

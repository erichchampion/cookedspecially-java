package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.SeatingTable;
import com.cookedspecially.kitchenservice.repository.SeatingTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for Seating Table operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatingTableServiceImpl implements SeatingTableService {

    private final SeatingTableRepository tableRepository;

    @Override
    public SeatingTable createTable(SeatingTable table) {
        log.info("Creating seating table: {}", table.getTableNumber());

        // Check if table with same number exists for restaurant
        if (tableRepository.existsByRestaurantIdAndTableNumberAndDeletedAtIsNull(
                table.getRestaurantId(), table.getTableNumber())) {
            throw new IllegalArgumentException("Table with number '" + table.getTableNumber() +
                "' already exists for this restaurant");
        }

        table.setStatus("AVAILABLE");
        table.setActive(true);

        return tableRepository.save(table);
    }

    @Override
    public SeatingTable updateTable(Long tableId, SeatingTable updatedTable) {
        log.info("Updating seating table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        // Check table number uniqueness if changed
        if (!table.getTableNumber().equals(updatedTable.getTableNumber())) {
            if (tableRepository.existsByRestaurantIdAndTableNumberAndDeletedAtIsNull(
                    table.getRestaurantId(), updatedTable.getTableNumber())) {
                throw new IllegalArgumentException("Table with number '" + updatedTable.getTableNumber() +
                    "' already exists");
            }
        }

        table.setTableNumber(updatedTable.getTableNumber());
        table.setName(updatedTable.getName());
        table.setCapacity(updatedTable.getCapacity());
        table.setSection(updatedTable.getSection());
        table.setNotes(updatedTable.getNotes());
        table.setUpdatedBy(updatedTable.getUpdatedBy());

        return tableRepository.save(table);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatingTable getTableById(Long tableId) {
        return tableRepository.findById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Seating table not found with ID: " + tableId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatingTable> getTablesByFulfillmentCenter(Long fulfillmentCenterId) {
        return tableRepository.findByFulfillmentCenterIdAndDeletedAtIsNull(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatingTable> getTablesByStatus(String status) {
        return tableRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatingTable> getAvailableTables(Long fulfillmentCenterId) {
        return tableRepository.findAvailableTables(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatingTable> getOccupiedTables(Long fulfillmentCenterId) {
        return tableRepository.findOccupiedTables(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatingTable getTableByNumber(Long restaurantId, String tableNumber) {
        return tableRepository.findByRestaurantIdAndTableNumberAndDeletedAtIsNull(restaurantId, tableNumber)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with number: " + tableNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatingTable getTableByQrCode(String qrCode) {
        return tableRepository.findByQrCodeAndDeletedAtIsNull(qrCode)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with QR code: " + qrCode));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatingTable getTableByOrderId(Long orderId) {
        return tableRepository.findByCurrentOrderIdAndDeletedAtIsNull(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with order ID: " + orderId));
    }

    @Override
    public SeatingTable occupyTable(Long tableId, Long orderId) {
        log.info("Occupying table {} with order {}", tableId, orderId);

        SeatingTable table = getTableById(tableId);

        if (!"AVAILABLE".equals(table.getStatus()) && !"RESERVED".equals(table.getStatus())) {
            throw new IllegalStateException("Table is not available for occupation");
        }

        table.occupy(orderId);
        return tableRepository.save(table);
    }

    @Override
    public SeatingTable releaseTable(Long tableId) {
        log.info("Releasing table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        if (!"OCCUPIED".equals(table.getStatus())) {
            throw new IllegalStateException("Table is not occupied");
        }

        table.release();
        return tableRepository.save(table);
    }

    @Override
    public SeatingTable reserveTable(Long tableId) {
        log.info("Reserving table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        if (!"AVAILABLE".equals(table.getStatus())) {
            throw new IllegalStateException("Table is not available for reservation");
        }

        table.reserve();
        return tableRepository.save(table);
    }

    @Override
    public SeatingTable markForCleaning(Long tableId) {
        log.info("Marking table {} for cleaning", tableId);

        SeatingTable table = getTableById(tableId);
        table.markForCleaning();

        return tableRepository.save(table);
    }

    @Override
    public SeatingTable markAvailable(Long tableId) {
        log.info("Marking table {} as available", tableId);

        SeatingTable table = getTableById(tableId);
        table.setStatus("AVAILABLE");
        table.setCurrentOrderId(null);
        table.setOccupiedSince(null);

        return tableRepository.save(table);
    }

    @Override
    public SeatingTable generateQrCode(Long tableId) {
        log.info("Generating QR code for table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        // Generate unique QR code
        String qrCode = "TBL-" + table.getRestaurantId() + "-" + table.getTableNumber() + "-" +
                        UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        table.setQrCode(qrCode);
        return tableRepository.save(table);
    }

    @Override
    public void deleteTable(Long tableId) {
        log.info("Deleting seating table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        if ("OCCUPIED".equals(table.getStatus())) {
            throw new IllegalStateException("Cannot delete an occupied table");
        }

        table.delete();
        tableRepository.save(table);
    }

    @Override
    public SeatingTable restoreTable(Long tableId) {
        log.info("Restoring seating table: {}", tableId);

        SeatingTable table = getTableById(tableId);

        if (table.getDeletedAt() == null) {
            throw new IllegalStateException("Table is not deleted");
        }

        table.setActive(true);
        table.setDeletedAt(null);
        table.setStatus("AVAILABLE");

        return tableRepository.save(table);
    }
}

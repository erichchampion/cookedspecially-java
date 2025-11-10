package com.cookedspecially.kitchenservice.controller;

import com.cookedspecially.kitchenservice.domain.SeatingTable;
import com.cookedspecially.kitchenservice.dto.CreateSeatingTableRequest;
import com.cookedspecially.kitchenservice.dto.OccupyTableRequest;
import com.cookedspecially.kitchenservice.dto.SeatingTableResponse;
import com.cookedspecially.kitchenservice.service.SeatingTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Seating Table operations
 */
@RestController
@RequestMapping("/api/v1/seating-tables")
@RequiredArgsConstructor
@Tag(name = "Seating Table Management", description = "Dine-in table management operations")
@SecurityRequirement(name = "bearer-jwt")
public class SeatingTableController {

    private final SeatingTableService tableService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Create a new seating table")
    public ResponseEntity<SeatingTableResponse> createTable(@Valid @RequestBody CreateSeatingTableRequest request) {
        SeatingTable table = SeatingTable.builder()
            .tableNumber(request.getTableNumber())
            .name(request.getName())
            .restaurantId(request.getRestaurantId())
            .fulfillmentCenterId(request.getFulfillmentCenterId())
            .capacity(request.getCapacity())
            .section(request.getSection())
            .notes(request.getNotes())
            .build();

        SeatingTable created = tableService.createTable(table);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Update seating table")
    public ResponseEntity<SeatingTableResponse> updateTable(@PathVariable Long id,
                                                             @Valid @RequestBody CreateSeatingTableRequest request) {
        SeatingTable table = SeatingTable.builder()
            .tableNumber(request.getTableNumber())
            .name(request.getName())
            .capacity(request.getCapacity())
            .section(request.getSection())
            .notes(request.getNotes())
            .build();

        SeatingTable updated = tableService.updateTable(id, table);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'WAITER')")
    @Operation(summary = "Get table by ID")
    public ResponseEntity<SeatingTableResponse> getTableById(@PathVariable Long id) {
        SeatingTable table = tableService.getTableById(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @GetMapping("/fulfillment-center/{fcId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'WAITER')")
    @Operation(summary = "Get tables by fulfillment center")
    public ResponseEntity<List<SeatingTableResponse>> getTablesByFulfillmentCenter(@PathVariable Long fcId) {
        List<SeatingTable> tables = tableService.getTablesByFulfillmentCenter(fcId);
        return ResponseEntity.ok(tables.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/fulfillment-center/{fcId}/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'WAITER', 'CUSTOMER')")
    @Operation(summary = "Get available tables")
    public ResponseEntity<List<SeatingTableResponse>> getAvailableTables(@PathVariable Long fcId) {
        List<SeatingTable> tables = tableService.getAvailableTables(fcId);
        return ResponseEntity.ok(tables.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/fulfillment-center/{fcId}/occupied")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'WAITER')")
    @Operation(summary = "Get occupied tables")
    public ResponseEntity<List<SeatingTableResponse>> getOccupiedTables(@PathVariable Long fcId) {
        List<SeatingTable> tables = tableService.getOccupiedTables(fcId);
        return ResponseEntity.ok(tables.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/number/{tableNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'WAITER', 'CUSTOMER')")
    @Operation(summary = "Get table by table number")
    public ResponseEntity<SeatingTableResponse> getTableByNumber(@PathVariable Long restaurantId,
                                                                  @PathVariable String tableNumber) {
        SeatingTable table = tableService.getTableByNumber(restaurantId, tableNumber);
        return ResponseEntity.ok(toResponse(table));
    }

    @GetMapping("/qr/{qrCode}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get table by QR code (public for mobile ordering)")
    public ResponseEntity<SeatingTableResponse> getTableByQrCode(@PathVariable String qrCode) {
        SeatingTable table = tableService.getTableByQrCode(qrCode);
        return ResponseEntity.ok(toResponse(table));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER', 'WAITER')")
    @Operation(summary = "Get table by order ID")
    public ResponseEntity<SeatingTableResponse> getTableByOrderId(@PathVariable Long orderId) {
        SeatingTable table = tableService.getTableByOrderId(orderId);
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/occupy")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER', 'WAITER')")
    @Operation(summary = "Occupy table with order")
    public ResponseEntity<SeatingTableResponse> occupyTable(@PathVariable Long id,
                                                             @Valid @RequestBody OccupyTableRequest request) {
        SeatingTable table = tableService.occupyTable(id, request.getOrderId());
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER', 'WAITER')")
    @Operation(summary = "Release table (order completed)")
    public ResponseEntity<SeatingTableResponse> releaseTable(@PathVariable Long id) {
        SeatingTable table = tableService.releaseTable(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    @Operation(summary = "Reserve table")
    public ResponseEntity<SeatingTableResponse> reserveTable(@PathVariable Long id) {
        SeatingTable table = tableService.reserveTable(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/cleaning")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    @Operation(summary = "Mark table for cleaning")
    public ResponseEntity<SeatingTableResponse> markForCleaning(@PathVariable Long id) {
        SeatingTable table = tableService.markForCleaning(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    @Operation(summary = "Mark table as available")
    public ResponseEntity<SeatingTableResponse> markAvailable(@PathVariable Long id) {
        SeatingTable table = tableService.markAvailable(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @PostMapping("/{id}/generate-qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Generate QR code for table")
    public ResponseEntity<SeatingTableResponse> generateQrCode(@PathVariable Long id) {
        SeatingTable table = tableService.generateQrCode(id);
        return ResponseEntity.ok(toResponse(table));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Delete seating table")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Restore deleted table")
    public ResponseEntity<SeatingTableResponse> restoreTable(@PathVariable Long id) {
        SeatingTable table = tableService.restoreTable(id);
        return ResponseEntity.ok(toResponse(table));
    }

    private SeatingTableResponse toResponse(SeatingTable table) {
        return SeatingTableResponse.builder()
            .id(table.getId())
            .tableNumber(table.getTableNumber())
            .name(table.getName())
            .restaurantId(table.getRestaurantId())
            .fulfillmentCenterId(table.getFulfillmentCenterId())
            .capacity(table.getCapacity())
            .status(table.getStatus())
            .section(table.getSection())
            .qrCode(table.getQrCode())
            .currentOrderId(table.getCurrentOrderId())
            .occupiedSince(table.getOccupiedSince())
            .active(table.getActive())
            .notes(table.getNotes())
            .createdAt(table.getCreatedAt())
            .updatedAt(table.getUpdatedAt())
            .build();
    }
}

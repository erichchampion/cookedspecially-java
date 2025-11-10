package com.cookedspecially.kitchenservice.controller;

import com.cookedspecially.kitchenservice.domain.KitchenScreen;
import com.cookedspecially.kitchenservice.dto.CreateKitchenScreenRequest;
import com.cookedspecially.kitchenservice.dto.KitchenScreenResponse;
import com.cookedspecially.kitchenservice.service.KitchenScreenService;
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
 * REST controller for Kitchen Screen operations
 */
@RestController
@RequestMapping("/api/v1/kitchen-screens")
@RequiredArgsConstructor
@Tag(name = "Kitchen Screen Management", description = "Kitchen display system operations")
@SecurityRequirement(name = "bearer-jwt")
public class KitchenScreenController {

    private final KitchenScreenService screenService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Create a new kitchen screen")
    public ResponseEntity<KitchenScreenResponse> createScreen(@Valid @RequestBody CreateKitchenScreenRequest request) {
        KitchenScreen screen = KitchenScreen.builder()
            .name(request.getName())
            .description(request.getDescription())
            .fulfillmentCenterId(request.getFulfillmentCenterId())
            .restaurantId(request.getRestaurantId())
            .stationType(request.getStationType())
            .ipAddress(request.getIpAddress())
            .deviceId(request.getDeviceId())
            .soundEnabled(request.getSoundEnabled())
            .autoAcceptOrders(request.getAutoAcceptOrders())
            .displayOrder(request.getDisplayOrder())
            .build();

        KitchenScreen created = screenService.createScreen(screen);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Update kitchen screen")
    public ResponseEntity<KitchenScreenResponse> updateScreen(@PathVariable Long id,
                                                               @Valid @RequestBody CreateKitchenScreenRequest request) {
        KitchenScreen screen = KitchenScreen.builder()
            .name(request.getName())
            .description(request.getDescription())
            .stationType(request.getStationType())
            .soundEnabled(request.getSoundEnabled())
            .autoAcceptOrders(request.getAutoAcceptOrders())
            .displayOrder(request.getDisplayOrder())
            .build();

        KitchenScreen updated = screenService.updateScreen(id, screen);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'KITCHEN_STAFF')")
    @Operation(summary = "Get kitchen screen by ID")
    public ResponseEntity<KitchenScreenResponse> getScreenById(@PathVariable Long id) {
        KitchenScreen screen = screenService.getScreenById(id);
        return ResponseEntity.ok(toResponse(screen));
    }

    @GetMapping("/fulfillment-center/{fcId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get screens by fulfillment center")
    public ResponseEntity<List<KitchenScreenResponse>> getScreensByFulfillmentCenter(@PathVariable Long fcId) {
        List<KitchenScreen> screens = screenService.getScreensByFulfillmentCenter(fcId);
        return ResponseEntity.ok(screens.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/fulfillment-center/{fcId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'KITCHEN_STAFF')")
    @Operation(summary = "Get active screens by fulfillment center")
    public ResponseEntity<List<KitchenScreenResponse>> getActiveScreens(@PathVariable Long fcId) {
        List<KitchenScreen> screens = screenService.getActiveScreens(fcId);
        return ResponseEntity.ok(screens.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/device/{deviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'KITCHEN_STAFF')")
    @Operation(summary = "Get screen by device ID")
    public ResponseEntity<KitchenScreenResponse> getScreenByDeviceId(@PathVariable String deviceId) {
        KitchenScreen screen = screenService.getScreenByDeviceId(deviceId);
        return ResponseEntity.ok(toResponse(screen));
    }

    @PostMapping("/{id}/heartbeat")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'KITCHEN_STAFF')")
    @Operation(summary = "Update screen heartbeat")
    public ResponseEntity<Void> updateHeartbeat(@PathVariable Long id) {
        screenService.updateHeartbeat(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/offline")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark screen as offline")
    public ResponseEntity<KitchenScreenResponse> markOffline(@PathVariable Long id) {
        KitchenScreen screen = screenService.markOffline(id);
        return ResponseEntity.ok(toResponse(screen));
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark screen as active")
    public ResponseEntity<KitchenScreenResponse> markActive(@PathVariable Long id) {
        KitchenScreen screen = screenService.markActive(id);
        return ResponseEntity.ok(toResponse(screen));
    }

    @PostMapping("/{id}/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark screen as under maintenance")
    public ResponseEntity<KitchenScreenResponse> markMaintenance(@PathVariable Long id) {
        KitchenScreen screen = screenService.markMaintenance(id);
        return ResponseEntity.ok(toResponse(screen));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Delete kitchen screen")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }

    private KitchenScreenResponse toResponse(KitchenScreen screen) {
        return KitchenScreenResponse.builder()
            .id(screen.getId())
            .name(screen.getName())
            .description(screen.getDescription())
            .fulfillmentCenterId(screen.getFulfillmentCenterId())
            .restaurantId(screen.getRestaurantId())
            .status(screen.getStatus())
            .stationType(screen.getStationType())
            .ipAddress(screen.getIpAddress())
            .deviceId(screen.getDeviceId())
            .soundEnabled(screen.getSoundEnabled())
            .autoAcceptOrders(screen.getAutoAcceptOrders())
            .displayOrder(screen.getDisplayOrder())
            .lastHeartbeat(screen.getLastHeartbeat())
            .isOnline(screen.isOnline())
            .createdAt(screen.getCreatedAt())
            .updatedAt(screen.getUpdatedAt())
            .build();
    }
}

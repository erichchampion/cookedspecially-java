package com.cookedspecially.kitchenservice.controller;

import com.cookedspecially.kitchenservice.domain.DeliveryBoy;
import com.cookedspecially.kitchenservice.domain.DeliveryBoyStatus;
import com.cookedspecially.kitchenservice.dto.CreateDeliveryBoyRequest;
import com.cookedspecially.kitchenservice.dto.DeliveryBoyResponse;
import com.cookedspecially.kitchenservice.dto.UpdateRatingRequest;
import com.cookedspecially.kitchenservice.service.DeliveryBoyService;
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
 * REST controller for Delivery Boy operations
 */
@RestController
@RequestMapping("/api/v1/delivery-boys")
@RequiredArgsConstructor
@Tag(name = "Delivery Boy Management", description = "Delivery personnel operations")
@SecurityRequirement(name = "bearer-jwt")
public class DeliveryBoyController {

    private final DeliveryBoyService deliveryBoyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Create a new delivery boy")
    public ResponseEntity<DeliveryBoyResponse> createDeliveryBoy(@Valid @RequestBody CreateDeliveryBoyRequest request) {
        DeliveryBoy deliveryBoy = DeliveryBoy.builder()
            .name(request.getName())
            .phone(request.getPhone())
            .email(request.getEmail())
            .restaurantId(request.getRestaurantId())
            .vehicleType(request.getVehicleType())
            .vehicleNumber(request.getVehicleNumber())
            .licenseNumber(request.getLicenseNumber())
            .notes(request.getNotes())
            .build();

        DeliveryBoy created = deliveryBoyService.createDeliveryBoy(deliveryBoy);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Update delivery boy")
    public ResponseEntity<DeliveryBoyResponse> updateDeliveryBoy(@PathVariable Long id,
                                                                  @Valid @RequestBody CreateDeliveryBoyRequest request) {
        DeliveryBoy deliveryBoy = DeliveryBoy.builder()
            .name(request.getName())
            .phone(request.getPhone())
            .email(request.getEmail())
            .vehicleType(request.getVehicleType())
            .vehicleNumber(request.getVehicleNumber())
            .licenseNumber(request.getLicenseNumber())
            .notes(request.getNotes())
            .build();

        DeliveryBoy updated = deliveryBoyService.updateDeliveryBoy(id, deliveryBoy);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get delivery boy by ID")
    public ResponseEntity<DeliveryBoyResponse> getDeliveryBoyById(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.getDeliveryBoyById(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get delivery boys by restaurant")
    public ResponseEntity<List<DeliveryBoyResponse>> getDeliveryBoysByRestaurant(@PathVariable Long restaurantId) {
        List<DeliveryBoy> deliveryBoys = deliveryBoyService.getDeliveryBoysByRestaurant(restaurantId);
        return ResponseEntity.ok(deliveryBoys.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get active delivery boys")
    public ResponseEntity<List<DeliveryBoyResponse>> getActiveDeliveryBoys(@PathVariable Long restaurantId) {
        List<DeliveryBoy> deliveryBoys = deliveryBoyService.getActiveDeliveryBoys(restaurantId);
        return ResponseEntity.ok(deliveryBoys.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER')")
    @Operation(summary = "Get available delivery boys for assignment")
    public ResponseEntity<List<DeliveryBoyResponse>> getAvailableDeliveryBoys(@PathVariable Long restaurantId) {
        List<DeliveryBoy> deliveryBoys = deliveryBoyService.getAvailableDeliveryBoys(restaurantId);
        return ResponseEntity.ok(deliveryBoys.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/top-rated")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get top rated delivery boys")
    public ResponseEntity<List<DeliveryBoyResponse>> getTopRatedDeliveryBoys(@PathVariable Long restaurantId) {
        List<DeliveryBoy> deliveryBoys = deliveryBoyService.getTopRatedDeliveryBoys(restaurantId);
        return ResponseEntity.ok(deliveryBoys.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER')")
    @Operation(summary = "Assign delivery to delivery boy")
    public ResponseEntity<DeliveryBoyResponse> assignDelivery(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.assignDelivery(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'MANAGER', 'DELIVERY_BOY')")
    @Operation(summary = "Complete delivery")
    public ResponseEntity<DeliveryBoyResponse> completeDelivery(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.completeDelivery(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DELIVERY_BOY')")
    @Operation(summary = "Update delivery boy status")
    public ResponseEntity<DeliveryBoyResponse> updateStatus(@PathVariable Long id,
                                                             @RequestParam DeliveryBoyStatus status) {
        DeliveryBoy deliveryBoy = deliveryBoyService.updateStatus(id, status);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PostMapping("/{id}/rating")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'CUSTOMER')")
    @Operation(summary = "Update delivery boy rating")
    public ResponseEntity<DeliveryBoyResponse> updateRating(@PathVariable Long id,
                                                             @Valid @RequestBody UpdateRatingRequest request) {
        DeliveryBoy deliveryBoy = deliveryBoyService.updateRating(id, request.getRating());
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PostMapping("/{id}/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DELIVERY_BOY')")
    @Operation(summary = "Mark delivery boy as available")
    public ResponseEntity<DeliveryBoyResponse> markAvailable(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.markAvailable(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PostMapping("/{id}/break")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DELIVERY_BOY')")
    @Operation(summary = "Mark delivery boy on break")
    public ResponseEntity<DeliveryBoyResponse> markOnBreak(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.markOnBreak(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @PostMapping("/{id}/offline")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DELIVERY_BOY')")
    @Operation(summary = "Mark delivery boy as offline")
    public ResponseEntity<DeliveryBoyResponse> markOffline(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.markOffline(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Delete delivery boy")
    public ResponseEntity<Void> deleteDeliveryBoy(@PathVariable Long id) {
        deliveryBoyService.deleteDeliveryBoy(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Restore deleted delivery boy")
    public ResponseEntity<DeliveryBoyResponse> restoreDeliveryBoy(@PathVariable Long id) {
        DeliveryBoy deliveryBoy = deliveryBoyService.restoreDeliveryBoy(id);
        return ResponseEntity.ok(toResponse(deliveryBoy));
    }

    private DeliveryBoyResponse toResponse(DeliveryBoy deliveryBoy) {
        return DeliveryBoyResponse.builder()
            .id(deliveryBoy.getId())
            .name(deliveryBoy.getName())
            .phone(deliveryBoy.getPhone())
            .email(deliveryBoy.getEmail())
            .restaurantId(deliveryBoy.getRestaurantId())
            .status(deliveryBoy.getStatus())
            .vehicleType(deliveryBoy.getVehicleType())
            .vehicleNumber(deliveryBoy.getVehicleNumber())
            .licenseNumber(deliveryBoy.getLicenseNumber())
            .currentDeliveryCount(deliveryBoy.getCurrentDeliveryCount())
            .totalDeliveriesCompleted(deliveryBoy.getTotalDeliveriesCompleted())
            .averageRating(deliveryBoy.getAverageRating())
            .ratingCount(deliveryBoy.getRatingCount())
            .notes(deliveryBoy.getNotes())
            .active(deliveryBoy.getActive())
            .createdAt(deliveryBoy.getCreatedAt())
            .updatedAt(deliveryBoy.getUpdatedAt())
            .build();
    }
}

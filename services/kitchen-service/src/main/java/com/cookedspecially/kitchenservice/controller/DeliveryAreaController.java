package com.cookedspecially.kitchenservice.controller;

import com.cookedspecially.kitchenservice.domain.DeliveryArea;
import com.cookedspecially.kitchenservice.dto.CreateDeliveryAreaRequest;
import com.cookedspecially.kitchenservice.dto.DeliveryAreaResponse;
import com.cookedspecially.kitchenservice.service.DeliveryAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Delivery Area operations
 */
@RestController
@RequestMapping("/api/v1/delivery-areas")
@RequiredArgsConstructor
@Tag(name = "Delivery Area Management", description = "Service area and delivery charge operations")
@SecurityRequirement(name = "bearer-jwt")
public class DeliveryAreaController {

    private final DeliveryAreaService deliveryAreaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Create a new delivery area")
    public ResponseEntity<DeliveryAreaResponse> createDeliveryArea(@Valid @RequestBody CreateDeliveryAreaRequest request) {
        DeliveryArea area = DeliveryArea.builder()
            .name(request.getName())
            .description(request.getDescription())
            .restaurantId(request.getRestaurantId())
            .deliveryCharge(request.getDeliveryCharge())
            .minimumOrderAmount(request.getMinimumOrderAmount())
            .freeDeliveryAbove(request.getFreeDeliveryAbove())
            .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
            .zipCode(request.getZipCode())
            .city(request.getCity())
            .state(request.getState())
            .country(request.getCountry())
            .displayOrder(request.getDisplayOrder())
            .build();

        DeliveryArea created = deliveryAreaService.createDeliveryArea(area);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Update delivery area")
    public ResponseEntity<DeliveryAreaResponse> updateDeliveryArea(@PathVariable Long id,
                                                                    @Valid @RequestBody CreateDeliveryAreaRequest request) {
        DeliveryArea area = DeliveryArea.builder()
            .name(request.getName())
            .description(request.getDescription())
            .deliveryCharge(request.getDeliveryCharge())
            .minimumOrderAmount(request.getMinimumOrderAmount())
            .freeDeliveryAbove(request.getFreeDeliveryAbove())
            .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
            .zipCode(request.getZipCode())
            .city(request.getCity())
            .state(request.getState())
            .country(request.getCountry())
            .displayOrder(request.getDisplayOrder())
            .build();

        DeliveryArea updated = deliveryAreaService.updateDeliveryArea(id, area);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get delivery area by ID")
    public ResponseEntity<DeliveryAreaResponse> getDeliveryAreaById(@PathVariable Long id) {
        DeliveryArea area = deliveryAreaService.getDeliveryAreaById(id);
        return ResponseEntity.ok(toResponse(area));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get delivery areas by restaurant")
    public ResponseEntity<List<DeliveryAreaResponse>> getDeliveryAreasByRestaurant(@PathVariable Long restaurantId) {
        List<DeliveryArea> areas = deliveryAreaService.getDeliveryAreasByRestaurant(restaurantId);
        return ResponseEntity.ok(areas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CUSTOMER', 'SYSTEM')")
    @Operation(summary = "Get active delivery areas")
    public ResponseEntity<List<DeliveryAreaResponse>> getActiveDeliveryAreas(@PathVariable Long restaurantId) {
        List<DeliveryArea> areas = deliveryAreaService.getActiveDeliveryAreas(restaurantId);
        return ResponseEntity.ok(areas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/search/zipcode")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CUSTOMER', 'SYSTEM')")
    @Operation(summary = "Find delivery areas by zip code")
    public ResponseEntity<List<DeliveryAreaResponse>> findByZipCode(@PathVariable Long restaurantId,
                                                                     @RequestParam String zipCode) {
        List<DeliveryArea> areas = deliveryAreaService.findDeliveryAreasByZipCode(restaurantId, zipCode);
        return ResponseEntity.ok(areas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}/search/city")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "Find delivery areas by city")
    public ResponseEntity<List<DeliveryAreaResponse>> findByCity(@PathVariable Long restaurantId,
                                                                  @RequestParam String city) {
        List<DeliveryArea> areas = deliveryAreaService.findDeliveryAreasByCity(restaurantId, city);
        return ResponseEntity.ok(areas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/charge")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'CUSTOMER')")
    @Operation(summary = "Calculate delivery charge for order amount")
    public ResponseEntity<BigDecimal> calculateDeliveryCharge(@PathVariable Long id,
                                                               @RequestParam BigDecimal orderAmount) {
        BigDecimal charge = deliveryAreaService.calculateDeliveryCharge(id, orderAmount);
        return ResponseEntity.ok(charge);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Delete delivery area")
    public ResponseEntity<Void> deleteDeliveryArea(@PathVariable Long id) {
        deliveryAreaService.deleteDeliveryArea(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Restore deleted delivery area")
    public ResponseEntity<DeliveryAreaResponse> restoreDeliveryArea(@PathVariable Long id) {
        DeliveryArea area = deliveryAreaService.restoreDeliveryArea(id);
        return ResponseEntity.ok(toResponse(area));
    }

    private DeliveryAreaResponse toResponse(DeliveryArea area) {
        return DeliveryAreaResponse.builder()
            .id(area.getId())
            .name(area.getName())
            .description(area.getDescription())
            .restaurantId(area.getRestaurantId())
            .deliveryCharge(area.getDeliveryCharge())
            .minimumOrderAmount(area.getMinimumOrderAmount())
            .freeDeliveryAbove(area.getFreeDeliveryAbove())
            .estimatedDeliveryTime(area.getEstimatedDeliveryTime())
            .zipCode(area.getZipCode())
            .city(area.getCity())
            .state(area.getState())
            .country(area.getCountry())
            .active(area.getActive())
            .displayOrder(area.getDisplayOrder())
            .createdAt(area.getCreatedAt())
            .updatedAt(area.getUpdatedAt())
            .build();
    }
}

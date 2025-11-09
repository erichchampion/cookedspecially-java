package com.cookedspecially.restaurantservice.controller;

import com.cookedspecially.restaurantservice.dto.CreateMenuItemRequest;
import com.cookedspecially.restaurantservice.dto.MenuItemResponse;
import com.cookedspecially.restaurantservice.dto.UpdateMenuItemRequest;
import com.cookedspecially.restaurantservice.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Menu Item Controller
 */
@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu Item", description = "Menu item management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class MenuItemController {

    private final MenuItemService menuItemService;

    /**
     * Create menu item
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Create menu item", description = "Create new menu item for restaurant (owner only)")
    public ResponseEntity<MenuItemResponse> createMenuItem(
        @Valid @RequestBody CreateMenuItemRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Creating menu item for restaurant {} by user: {}", request.getRestaurantId(), userId);

        MenuItemResponse response = menuItemService.createMenuItem(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get menu item by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID", description = "Retrieve menu item details by ID")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long id) {
        log.info("Fetching menu item: {}", id);

        MenuItemResponse response = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update menu item
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Update menu item", description = "Update menu item details (owner only)")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
        @PathVariable Long id,
        @Valid @RequestBody UpdateMenuItemRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Updating menu item {} by user: {}", id, userId);

        MenuItemResponse response = menuItemService.updateMenuItem(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete menu item
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Delete menu item", description = "Delete menu item (owner only)")
    public ResponseEntity<Void> deleteMenuItem(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Deleting menu item {} by user: {}", id, userId);

        menuItemService.deleteMenuItem(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get menu items for restaurant
     */
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get menu items", description = "Get all menu items for restaurant")
    public ResponseEntity<Page<MenuItemResponse>> getRestaurantMenuItems(
        @PathVariable Long restaurantId,
        Pageable pageable
    ) {
        log.info("Fetching menu items for restaurant: {}", restaurantId);

        Page<MenuItemResponse> menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get available menu items
     */
    @GetMapping("/restaurant/{restaurantId}/available")
    @Operation(summary = "Get available menu items", description = "Get only available menu items for restaurant")
    public ResponseEntity<Page<MenuItemResponse>> getAvailableMenuItems(
        @PathVariable Long restaurantId,
        Pageable pageable
    ) {
        log.info("Fetching available menu items for restaurant: {}", restaurantId);

        Page<MenuItemResponse> menuItems = menuItemService.getAvailableMenuItems(restaurantId, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items by category
     */
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    @Operation(summary = "Get menu items by category", description = "Filter menu items by category")
    public ResponseEntity<Page<MenuItemResponse>> getMenuItemsByCategory(
        @PathVariable Long restaurantId,
        @PathVariable String category,
        Pageable pageable
    ) {
        log.info("Fetching menu items for restaurant {} in category: {}", restaurantId, category);

        Page<MenuItemResponse> menuItems = menuItemService.getMenuItemsByCategory(restaurantId, category, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get vegetarian menu items
     */
    @GetMapping("/restaurant/{restaurantId}/vegetarian")
    @Operation(summary = "Get vegetarian items", description = "Get vegetarian menu items")
    public ResponseEntity<Page<MenuItemResponse>> getVegetarianItems(
        @PathVariable Long restaurantId,
        Pageable pageable
    ) {
        log.info("Fetching vegetarian items for restaurant: {}", restaurantId);

        Page<MenuItemResponse> menuItems = menuItemService.getVegetarianItems(restaurantId, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get vegan menu items
     */
    @GetMapping("/restaurant/{restaurantId}/vegan")
    @Operation(summary = "Get vegan items", description = "Get vegan menu items")
    public ResponseEntity<Page<MenuItemResponse>> getVeganItems(
        @PathVariable Long restaurantId,
        Pageable pageable
    ) {
        log.info("Fetching vegan items for restaurant: {}", restaurantId);

        Page<MenuItemResponse> menuItems = menuItemService.getVeganItems(restaurantId, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get gluten-free menu items
     */
    @GetMapping("/restaurant/{restaurantId}/gluten-free")
    @Operation(summary = "Get gluten-free items", description = "Get gluten-free menu items")
    public ResponseEntity<Page<MenuItemResponse>> getGlutenFreeItems(
        @PathVariable Long restaurantId,
        Pageable pageable
    ) {
        log.info("Fetching gluten-free items for restaurant: {}", restaurantId);

        Page<MenuItemResponse> menuItems = menuItemService.getGlutenFreeItems(restaurantId, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Search menu items
     */
    @GetMapping("/restaurant/{restaurantId}/search")
    @Operation(summary = "Search menu items", description = "Search menu items by name")
    public ResponseEntity<Page<MenuItemResponse>> searchMenuItems(
        @PathVariable Long restaurantId,
        @RequestParam String query,
        Pageable pageable
    ) {
        log.info("Searching menu items for restaurant {}: {}", restaurantId, query);

        Page<MenuItemResponse> menuItems = menuItemService.searchMenuItems(restaurantId, query, pageable);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Update menu item availability
     */
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Update availability", description = "Update menu item availability (owner only)")
    public ResponseEntity<MenuItemResponse> updateAvailability(
        @PathVariable Long id,
        @RequestParam boolean available,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Updating menu item {} availability to {} by user: {}", id, available, userId);

        MenuItemResponse response = menuItemService.updateAvailability(id, available, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Bulk update availability
     */
    @PatchMapping("/bulk-availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Bulk update availability", description = "Update availability for multiple items (owner only)")
    public ResponseEntity<Void> bulkUpdateAvailability(
        @RequestParam List<Long> itemIds,
        @RequestParam boolean available,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Bulk updating {} menu items availability to {} by user: {}", itemIds.size(), available, userId);

        menuItemService.bulkUpdateAvailability(itemIds, available, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get menu categories for restaurant
     */
    @GetMapping("/restaurant/{restaurantId}/categories")
    @Operation(summary = "Get categories", description = "Get all menu categories for restaurant")
    public ResponseEntity<List<String>> getCategories(@PathVariable Long restaurantId) {
        log.info("Fetching categories for restaurant: {}", restaurantId);

        List<String> categories = menuItemService.getCategories(restaurantId);
        return ResponseEntity.ok(categories);
    }
}

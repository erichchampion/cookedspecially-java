package com.cookedspecially.restaurantservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.restaurantservice.domain.MenuItem;
import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.cookedspecially.restaurantservice.dto.CreateMenuItemRequest;
import com.cookedspecially.restaurantservice.dto.MenuItemResponse;
import com.cookedspecially.restaurantservice.dto.UpdateMenuItemRequest;
import com.cookedspecially.restaurantservice.event.RestaurantEventPublisher;
import com.cookedspecially.restaurantservice.exception.MenuItemNotFoundException;
import com.cookedspecially.restaurantservice.exception.RestaurantNotFoundException;
import com.cookedspecially.restaurantservice.exception.UnauthorizedAccessException;
import com.cookedspecially.restaurantservice.repository.MenuItemRepository;
import com.cookedspecially.restaurantservice.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu Item Service
 */
@Service
public class MenuItemService {

    private static final Logger log = LoggerFactory.getLogger(MenuItemService.class);

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    // Constructor
    public MenuItemService(MenuItemRepository menuItemRepository,
                 RestaurantRepository restaurantRepository,
                 RestaurantEventPublisher eventPublisher) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create menu item
     */
    @Transactional
    @CacheEvict(value = {"menuItems", "restaurants"}, allEntries = true)
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request, Long userId) {
        log.info("Creating menu item for restaurant: {}", request.restaurantId());

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
            .orElseThrow(() -> new RestaurantNotFoundException(request.restaurantId()));

        // Verify ownership
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, request.restaurantId());
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setImageUrl(request.imageUrl());
        menuItem.setIsAvailable(request.isAvailable());
        menuItem.setIsVegetarian(request.isVegetarian());
        menuItem.setIsVegan(request.isVegan());
        menuItem.setIsGlutenFree(request.isGlutenFree());
        menuItem.setCalories(request.calories());
        menuItem.setPreparationTimeMinutes(request.preparationTimeMinutes());

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Created menu item with ID: {}", saved.getId());

        // Publish event
        eventPublisher.publishMenuUpdated(restaurant);

        return MenuItemResponse.fromEntity(saved);
    }

    /**
     * Get menu item by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "menuItems", key = "#menuItemId")
    public MenuItemResponse getMenuItemById(Long menuItemId) {
        log.debug("Fetching menu item: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

        return MenuItemResponse.fromEntity(menuItem);
    }

    /**
     * Update menu item
     */
    @Transactional
    @CacheEvict(value = {"menuItems", "restaurants"}, key = "#menuItemId")
    public MenuItemResponse updateMenuItem(Long menuItemId, UpdateMenuItemRequest request, Long userId) {
        log.info("Updating menu item: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

        // Verify ownership
        if (!menuItem.getRestaurant().getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, menuItemId);
        }

        // Update fields if provided
        if (request.name() != null) {
            menuItem.setName(request.name());
        }
        if (request.description() != null) {
            menuItem.setDescription(request.description());
        }
        if (request.price() != null) {
            menuItem.setPrice(request.price());
        }
        if (request.category() != null) {
            menuItem.setCategory(request.category());
        }
        if (request.imageUrl() != null) {
            menuItem.setImageUrl(request.imageUrl());
        }
        if (request.isAvailable() != null) {
            menuItem.setIsAvailable(request.isAvailable());
        }
        if (request.isVegetarian() != null) {
            menuItem.setIsVegetarian(request.isVegetarian());
        }
        if (request.isVegan() != null) {
            menuItem.setIsVegan(request.isVegan());
        }
        if (request.isGlutenFree() != null) {
            menuItem.setIsGlutenFree(request.isGlutenFree());
        }
        if (request.calories() != null) {
            menuItem.setCalories(request.calories());
        }
        if (request.preparationTimeMinutes() != null) {
            menuItem.setPreparationTimeMinutes(request.preparationTimeMinutes());
        }
        if (request.spiceLevel() != null) {
            menuItem.setSpiceLevel(request.spiceLevel());
        }

        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Updated menu item: {}", menuItemId);

        // Publish event
        eventPublisher.publishMenuUpdated(menuItem.getRestaurant());

        return MenuItemResponse.fromEntity(updated);
    }

    /**
     * Delete menu item
     */
    @Transactional
    @CacheEvict(value = {"menuItems", "restaurants"}, allEntries = true)
    public void deleteMenuItem(Long menuItemId, Long userId) {
        log.info("Deleting menu item: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

        // Verify ownership
        if (!menuItem.getRestaurant().getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, menuItemId);
        }

        Restaurant restaurant = menuItem.getRestaurant();

        menuItemRepository.delete(menuItem);
        log.info("Deleted menu item: {}", menuItemId);

        // Publish event
        eventPublisher.publishMenuUpdated(restaurant);
    }

    /**
     * Get menu items by restaurant
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuItemsByRestaurant(Long restaurantId, Pageable pageable) {
        log.debug("Fetching menu items for restaurant: {}", restaurantId);

        // Verify restaurant exists
        restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        Page<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Get available menu items by restaurant
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getAvailableMenuItems(Long restaurantId, Pageable pageable) {
        log.debug("Fetching available menu items for restaurant: {}", restaurantId);

        // Verify restaurant exists
        restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        Page<MenuItem> menuItems = menuItemRepository
            .findByRestaurantIdAndIsAvailableTrue(restaurantId, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Get menu items by category
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuItemsByCategory(Long restaurantId, String category, Pageable pageable) {
        log.debug("Fetching menu items for restaurant {} in category: {}", restaurantId, category);

        // Verify restaurant exists
        restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        Page<MenuItem> menuItems = menuItemRepository
            .findByRestaurantIdAndCategoryAndIsAvailableTrue(restaurantId, category, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Get vegetarian menu items
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getVegetarianItems(Long restaurantId, Pageable pageable) {
        log.debug("Fetching vegetarian items for restaurant: {}", restaurantId);

        Page<MenuItem> menuItems = menuItemRepository
            .findByRestaurantIdAndIsVegetarianTrueAndIsAvailableTrue(restaurantId, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Get vegan menu items
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getVeganItems(Long restaurantId, Pageable pageable) {
        log.debug("Fetching vegan items for restaurant: {}", restaurantId);

        Page<MenuItem> menuItems = menuItemRepository
            .findByRestaurantIdAndIsVeganTrueAndIsAvailableTrue(restaurantId, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Get gluten-free menu items
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getGlutenFreeItems(Long restaurantId, Pageable pageable) {
        log.debug("Fetching gluten-free items for restaurant: {}", restaurantId);

        Page<MenuItem> menuItems = menuItemRepository
            .findByRestaurantIdAndIsGlutenFreeTrueAndIsAvailableTrue(restaurantId, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Search menu items by name
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> searchMenuItems(Long restaurantId, String searchTerm, Pageable pageable) {
        log.debug("Searching menu items for restaurant {}: {}", restaurantId, searchTerm);

        Page<MenuItem> menuItems = menuItemRepository
            .searchByName(restaurantId, searchTerm, pageable);

        return menuItems.map(MenuItemResponse::fromEntity);
    }

    /**
     * Update menu item availability
     */
    @Transactional
    @CacheEvict(value = {"menuItems", "restaurants"}, key = "#menuItemId")
    public MenuItemResponse updateAvailability(Long menuItemId, boolean available, Long userId) {
        log.info("Updating menu item {} availability to: {}", menuItemId, available);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

        // Verify ownership
        if (!menuItem.getRestaurant().getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, menuItemId);
        }

        menuItem.setIsAvailable(available);
        MenuItem updated = menuItemRepository.save(menuItem);

        log.info("Updated menu item {} availability", menuItemId);

        // Publish event
        eventPublisher.publishMenuUpdated(menuItem.getRestaurant());

        return MenuItemResponse.fromEntity(updated);
    }

    /**
     * Bulk update menu item availability
     */
    @Transactional
    @CacheEvict(value = {"menuItems", "restaurants"}, allEntries = true)
    public void bulkUpdateAvailability(List<Long> menuItemIds, boolean available, Long userId) {
        log.info("Bulk updating {} menu items availability to: {}", menuItemIds.size(), available);

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);

        // Verify all items belong to restaurants owned by user
        for (MenuItem item : menuItems) {
            if (!item.getRestaurant().getOwnerId().equals(userId)) {
                throw new UnauthorizedAccessException(userId, item.getId());
            }
        }

        menuItems.forEach(item -> item.setIsAvailable(available));
        menuItemRepository.saveAll(menuItems);

        log.info("Bulk updated {} menu items", menuItems.size());

        // Publish events for all affected restaurants
        menuItems.stream()
            .map(MenuItem::getRestaurant)
            .distinct()
            .forEach(eventPublisher::publishMenuUpdated);
    }

    /**
     * Get all categories for a restaurant
     */
    @Transactional(readOnly = true)
    public List<String> getCategories(Long restaurantId) {
        log.debug("Fetching categories for restaurant: {}", restaurantId);

        // Verify restaurant exists
        restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        List<MenuItem> allItems = menuItemRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId);

        return allItems.stream()
            .map(MenuItem::getCategory)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}

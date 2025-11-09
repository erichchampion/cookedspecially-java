package com.cookedspecially.restaurantservice.exception;

/**
 * Exception thrown when menu item is not found
 */
public class MenuItemNotFoundException extends RuntimeException {
    private final Long menuItemId;

    public MenuItemNotFoundException(Long menuItemId) {
        super("Menu item not found with ID: " + menuItemId);
        this.menuItemId = menuItemId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }
}

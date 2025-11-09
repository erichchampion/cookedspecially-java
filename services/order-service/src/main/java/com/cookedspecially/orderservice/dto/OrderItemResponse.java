package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderItem;

import java.math.BigDecimal;

/**
 * Order Item Response DTO
 */
public class OrderItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String specialInstructions;

    // Constructors
    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, Long menuItemId, String menuItemName, Integer quantity,
                             BigDecimal unitPrice, BigDecimal totalPrice, String specialInstructions) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.specialInstructions = specialInstructions;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getMenuItemId(),
            item.getMenuItemName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice(),
            item.getSpecialInstructions()
        );
    }
}

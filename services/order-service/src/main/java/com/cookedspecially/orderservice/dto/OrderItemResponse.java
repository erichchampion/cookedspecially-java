package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Order Item Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String specialInstructions;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
            .id(item.getId())
            .menuItemId(item.getMenuItemId())
            .menuItemName(item.getMenuItemName())
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .totalPrice(item.getTotalPrice())
            .specialInstructions(item.getSpecialInstructions())
            .build();
    }
}

package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.Order;
import com.cookedspecially.orderservice.domain.OrderStatus;
import com.cookedspecially.orderservice.domain.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private OrderType orderType;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal deliveryCharge;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String deliveryAddress;
    private String specialInstructions;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime deliveredAt;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(order.getCustomerId())
            .restaurantId(order.getRestaurantId())
            .status(order.getStatus())
            .orderType(order.getOrderType())
            .subtotal(order.getSubtotal())
            .taxAmount(order.getTaxAmount())
            .deliveryCharge(order.getDeliveryCharge())
            .discountAmount(order.getDiscountAmount())
            .totalAmount(order.getTotalAmount())
            .paymentMethod(order.getPaymentMethod())
            .paymentStatus(order.getPaymentStatus())
            .deliveryAddress(order.getDeliveryAddress())
            .specialInstructions(order.getSpecialInstructions())
            .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
            .deliveredAt(order.getDeliveredAt())
            .items(order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList()))
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}

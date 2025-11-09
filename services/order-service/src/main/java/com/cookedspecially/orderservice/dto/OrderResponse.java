package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.Order;
import com.cookedspecially.orderservice.domain.OrderStatus;
import com.cookedspecially.orderservice.domain.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Response DTO
 */
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

    // Constructors
    public OrderResponse() {
    }

    public OrderResponse(Long id, String orderNumber, Long customerId, Long restaurantId,
                         OrderStatus status, OrderType orderType, BigDecimal subtotal, BigDecimal taxAmount,
                         BigDecimal deliveryCharge, BigDecimal discountAmount, BigDecimal totalAmount,
                         String paymentMethod, String paymentStatus, String deliveryAddress,
                         String specialInstructions, LocalDateTime estimatedDeliveryTime,
                         LocalDateTime deliveredAt, List<OrderItemResponse> items,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.orderType = orderType;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.deliveryCharge = deliveryCharge;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.deliveredAt = deliveredAt;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getCustomerId(),
            order.getRestaurantId(),
            order.getStatus(),
            order.getOrderType(),
            order.getSubtotal(),
            order.getTaxAmount(),
            order.getDeliveryCharge(),
            order.getDiscountAmount(),
            order.getTotalAmount(),
            order.getPaymentMethod(),
            order.getPaymentStatus(),
            order.getDeliveryAddress(),
            order.getSpecialInstructions(),
            order.getEstimatedDeliveryTime(),
            order.getDeliveredAt(),
            order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList()),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}

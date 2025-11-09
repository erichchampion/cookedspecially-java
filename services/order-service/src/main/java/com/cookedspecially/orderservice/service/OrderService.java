package com.cookedspecially.orderservice.service;

import com.cookedspecially.orderservice.domain.Order;
import com.cookedspecially.orderservice.domain.OrderItem;
import com.cookedspecially.orderservice.domain.OrderStatus;
import com.cookedspecially.orderservice.dto.CreateOrderRequest;
import com.cookedspecially.orderservice.dto.OrderItemRequest;
import com.cookedspecially.orderservice.dto.OrderResponse;
import com.cookedspecially.orderservice.event.OrderEventPublisher;
import com.cookedspecially.orderservice.exception.InvalidOrderStateException;
import com.cookedspecially.orderservice.exception.OrderNotFoundException;
import com.cookedspecially.orderservice.exception.OrderValidationException;
import com.cookedspecially.orderservice.repository.OrderRepository;
import com.cookedspecially.orderservice.util.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service - Core business logic for order management
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    private final OrderEventPublisher eventPublisher;

    /**
     * Create a new order
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}, restaurant: {}",
            request.getCustomerId(), request.getRestaurantId());

        // Validate order request
        validateOrderRequest(request);

        // Generate unique order number
        String orderNumber = generateUniqueOrderNumber();

        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(
            request.getSubtotal(),
            request.getTaxAmount(),
            request.getDeliveryCharge(),
            request.getDiscountAmount()
        );

        // Create order entity
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .customerId(request.getCustomerId())
            .restaurantId(request.getRestaurantId())
            .status(OrderStatus.PENDING)
            .orderType(request.getOrderType())
            .subtotal(request.getSubtotal())
            .taxAmount(request.getTaxAmount())
            .deliveryCharge(request.getDeliveryCharge())
            .discountAmount(request.getDiscountAmount())
            .totalAmount(totalAmount)
            .paymentMethod(request.getPaymentMethod())
            .paymentStatus("PENDING")
            .deliveryAddress(request.getDeliveryAddress())
            .specialInstructions(request.getSpecialInstructions())
            .estimatedDeliveryTime(calculateEstimatedDeliveryTime())
            .build();

        // Add order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = createOrderItem(itemRequest);
            order.addItem(item);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", orderNumber);

        // Publish order created event
        eventPublisher.publishOrderCreated(savedOrder);

        return OrderResponse.fromEntity(savedOrder);
    }

    /**
     * Get order by ID
     */
    @Cacheable(value = "orders", key = "#orderId")
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.debug("Fetching order by ID: {}", orderId);
        Order order = findOrderById(orderId);
        return OrderResponse.fromEntity(order);
    }

    /**
     * Get order by order number
     */
    @Cacheable(value = "orders", key = "#orderNumber")
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return OrderResponse.fromEntity(order);
    }

    /**
     * Get orders for a customer
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCustomerOrders(Long customerId, Pageable pageable) {
        log.debug("Fetching orders for customer: {}", customerId);
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(OrderResponse::fromEntity);
    }

    /**
     * Get orders for a restaurant
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrders(Long restaurantId, Pageable pageable) {
        log.debug("Fetching orders for restaurant: {}", restaurantId);
        Page<Order> orders = orderRepository.findByRestaurantId(restaurantId, pageable);
        return orders.map(OrderResponse::fromEntity);
    }

    /**
     * Get active orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders() {
        log.debug("Fetching active orders");
        List<Order> orders = orderRepository.findActiveOrders();
        return orders.stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get pending orders for a restaurant
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getPendingOrdersForRestaurant(Long restaurantId) {
        log.debug("Fetching pending orders for restaurant: {}", restaurantId);
        List<Order> orders = orderRepository.findPendingOrdersForRestaurant(restaurantId);
        return orders.stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Order order = findOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();

        // Validate state transition
        if (!order.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateException(oldStatus, newStatus);
        }

        // Update status
        order.setStatus(newStatus);

        // Update delivered timestamp if order is delivered
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);

        // Publish order status changed event
        eventPublisher.publishOrderStatusChanged(updatedOrder, oldStatus, newStatus);

        return OrderResponse.fromEntity(updatedOrder);
    }

    /**
     * Confirm order
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse confirmOrder(Long orderId) {
        log.info("Confirming order: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    /**
     * Start preparing order
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse startPreparingOrder(Long orderId) {
        log.info("Starting preparation for order: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.PREPARING);
    }

    /**
     * Mark order as ready
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse markOrderReady(Long orderId) {
        log.info("Marking order as ready: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.READY);
    }

    /**
     * Mark order as out for delivery
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse markOrderOutForDelivery(Long orderId) {
        log.info("Marking order as out for delivery: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY);
    }

    /**
     * Mark order as delivered
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse markOrderDelivered(Long orderId) {
        log.info("Marking order as delivered: {}", orderId);
        return updateOrderStatus(orderId, OrderStatus.DELIVERED);
    }

    /**
     * Cancel order
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {} with reason: {}", orderId, reason);

        Order order = findOrderById(orderId);

        // Validate that order can be cancelled
        if (!order.canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidOrderStateException(order.getStatus(), OrderStatus.CANCELLED);
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);

        // Publish order cancelled event
        eventPublisher.publishOrderCancelled(cancelledOrder, reason);

        return OrderResponse.fromEntity(cancelledOrder);
    }

    /**
     * Update payment status
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderResponse updatePaymentStatus(Long orderId, String paymentStatus) {
        log.info("Updating payment status for order {} to {}", orderId, paymentStatus);

        Order order = findOrderById(orderId);
        order.setPaymentStatus(paymentStatus);

        Order updatedOrder = orderRepository.save(order);
        log.info("Payment status updated for order: {}", orderId);

        // Publish payment status changed event
        eventPublisher.publishPaymentStatusChanged(updatedOrder, paymentStatus);

        return OrderResponse.fromEntity(updatedOrder);
    }

    // ==================== Private Helper Methods ====================

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private void validateOrderRequest(CreateOrderRequest request) {
        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new OrderValidationException("items", "Order must contain at least one item");
        }

        // Validate subtotal calculation
        BigDecimal calculatedSubtotal = request.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (calculatedSubtotal.compareTo(request.getSubtotal()) != 0) {
            throw new OrderValidationException("subtotal",
                "Subtotal does not match sum of item prices");
        }

        // Additional validations can be added here
        // - Validate customer exists (call Customer Service)
        // - Validate restaurant exists (call Restaurant Service)
        // - Validate menu items exist (call Menu Service)
        // - Validate delivery address for delivery orders
    }

    private String generateUniqueOrderNumber() {
        String orderNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            orderNumber = orderNumberGenerator.generate();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new OrderValidationException(
                    "Failed to generate unique order number after " + maxAttempts + " attempts");
            }
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());

        return orderNumber;
    }

    private BigDecimal calculateTotalAmount(
            BigDecimal subtotal,
            BigDecimal taxAmount,
            BigDecimal deliveryCharge,
            BigDecimal discountAmount) {

        return subtotal
            .add(taxAmount)
            .add(deliveryCharge)
            .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    private LocalDateTime calculateEstimatedDeliveryTime() {
        // Simple calculation: current time + 45 minutes
        // In production, this would be more sophisticated based on:
        // - Restaurant preparation time
        // - Distance to delivery address
        // - Current order volume
        // - Delivery partner availability
        return LocalDateTime.now().plusMinutes(45);
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        BigDecimal totalPrice = request.getUnitPrice()
            .multiply(BigDecimal.valueOf(request.getQuantity()));

        return OrderItem.builder()
            .menuItemId(request.getMenuItemId())
            .menuItemName(request.getMenuItemName())
            .quantity(request.getQuantity())
            .unitPrice(request.getUnitPrice())
            .totalPrice(totalPrice)
            .specialInstructions(request.getSpecialInstructions())
            .build();
    }
}

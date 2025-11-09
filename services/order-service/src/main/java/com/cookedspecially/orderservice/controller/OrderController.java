package com.cookedspecially.orderservice.controller;

import com.cookedspecially.orderservice.domain.OrderStatus;
import com.cookedspecially.orderservice.dto.*;
import com.cookedspecially.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order REST API Controller
 *
 * Provides RESTful endpoints for order management
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating order for customer: {}", request.getCustomerId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("GET /api/orders/{} - Fetching order", orderId);
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieve order details by order number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(
            @Parameter(description = "Order Number") @PathVariable String orderNumber) {
        log.info("GET /api/orders/number/{} - Fetching order", orderNumber);
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer orders
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer orders", description = "Retrieve all orders for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<Page<OrderResponse>> getCustomerOrders(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("GET /api/orders/customer/{} - Fetching customer orders", customerId);
        Page<OrderResponse> response = orderService.getCustomerOrders(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurant orders
     */
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get restaurant orders", description = "Retrieve all orders for a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrders(
            @Parameter(description = "Restaurant ID") @PathVariable Long restaurantId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("GET /api/orders/restaurant/{} - Fetching restaurant orders", restaurantId);
        Page<OrderResponse> response = orderService.getRestaurantOrders(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get active orders
     */
    @GetMapping("/active")
    @Operation(summary = "Get active orders", description = "Retrieve all active orders (not delivered or cancelled)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active orders retrieved successfully")
    })
    public ResponseEntity<List<OrderResponse>> getActiveOrders() {
        log.info("GET /api/orders/active - Fetching active orders");
        List<OrderResponse> response = orderService.getActiveOrders();
        return ResponseEntity.ok(response);
    }

    /**
     * Get pending orders for restaurant
     */
    @GetMapping("/restaurant/{restaurantId}/pending")
    @Operation(summary = "Get pending orders for restaurant", description = "Retrieve pending orders for a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending orders retrieved successfully")
    })
    public ResponseEntity<List<OrderResponse>> getPendingOrdersForRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable Long restaurantId) {
        log.info("GET /api/orders/restaurant/{}/pending - Fetching pending orders", restaurantId);
        List<OrderResponse> response = orderService.getPendingOrdersForRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status
     */
    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("PATCH /api/orders/{}/status - Updating to {}", orderId, request.getStatus());
        OrderResponse response = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * Confirm order
     */
    @PostMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm order", description = "Confirm a pending order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> confirmOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("POST /api/orders/{}/confirm - Confirming order", orderId);
        OrderResponse response = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Start preparing order
     */
    @PostMapping("/{orderId}/prepare")
    @Operation(summary = "Start preparing order", description = "Mark order as being prepared")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order preparation started"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> startPreparingOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("POST /api/orders/{}/prepare - Starting preparation", orderId);
        OrderResponse response = orderService.startPreparingOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark order as ready
     */
    @PostMapping("/{orderId}/ready")
    @Operation(summary = "Mark order as ready", description = "Mark order as ready for delivery/pickup")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order marked as ready"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> markOrderReady(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("POST /api/orders/{}/ready - Marking as ready", orderId);
        OrderResponse response = orderService.markOrderReady(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark order as out for delivery
     */
    @PostMapping("/{orderId}/out-for-delivery")
    @Operation(summary = "Mark order out for delivery", description = "Mark order as out for delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order marked as out for delivery"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> markOrderOutForDelivery(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("POST /api/orders/{}/out-for-delivery - Marking as out for delivery", orderId);
        OrderResponse response = orderService.markOrderOutForDelivery(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark order as delivered
     */
    @PostMapping("/{orderId}/delivered")
    @Operation(summary = "Mark order as delivered", description = "Mark order as delivered to customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order marked as delivered"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> markOrderDelivered(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("POST /api/orders/{}/delivered - Marking as delivered", orderId);
        OrderResponse response = orderService.markOrderDelivered(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel order
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequest request) {
        log.info("POST /api/orders/{}/cancel - Cancelling order", orderId);
        OrderResponse response = orderService.cancelOrder(orderId, request.getReason());
        return ResponseEntity.ok(response);
    }
}

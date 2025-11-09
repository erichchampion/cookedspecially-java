package com.cookedspecially.orderservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.orderservice.domain.Order;
import com.cookedspecially.orderservice.domain.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order Event Publisher
 *
 * Publishes order lifecycle events to AWS SNS topics
 */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final SnsClient snsClient;

    // Constructor
    public OrderEventPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.order-events-topic-arn}")
    private String orderEventsTopicArn;

    /**
     * Publish order created event
     */
    @Async
    public void publishOrderCreated(Order order) {
        log.info("Publishing ORDER_CREATED event for order: {}", order.getOrderNumber());

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.ORDER_CREATED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(order.getStatus());
        event.setTotalAmount(order.getTotalAmount());

        publishEvent(event, "OrderCreated");
    }

    /**
     * Publish order status changed event
     */
    @Async
    public void publishOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        log.info("Publishing ORDER_STATUS_CHANGED event for order: {} ({} -> {})",
            order.getOrderNumber(), oldStatus, newStatus);

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.ORDER_STATUS_CHANGED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(newStatus);
        event.setPreviousStatus(oldStatus);
        event.setTotalAmount(order.getTotalAmount());

        publishEvent(event, "OrderStatusChanged");

        // Publish specific event types for key status changes
        if (newStatus == OrderStatus.CONFIRMED) {
            publishOrderConfirmed(order);
        } else if (newStatus == OrderStatus.DELIVERED) {
            publishOrderDelivered(order);
        }
    }

    /**
     * Publish order confirmed event
     */
    @Async
    public void publishOrderConfirmed(Order order) {
        log.info("Publishing ORDER_CONFIRMED event for order: {}", order.getOrderNumber());

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.ORDER_CONFIRMED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(order.getStatus());
        event.setTotalAmount(order.getTotalAmount());

        publishEvent(event, "OrderConfirmed");
    }

    /**
     * Publish order cancelled event
     */
    @Async
    public void publishOrderCancelled(Order order, String reason) {
        log.info("Publishing ORDER_CANCELLED event for order: {}", order.getOrderNumber());

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.ORDER_CANCELLED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(order.getStatus());
        event.setTotalAmount(order.getTotalAmount());
        event.setCancellationReason(reason);

        publishEvent(event, "OrderCancelled");
    }

    /**
     * Publish order delivered event
     */
    @Async
    public void publishOrderDelivered(Order order) {
        log.info("Publishing ORDER_DELIVERED event for order: {}", order.getOrderNumber());

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.ORDER_DELIVERED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(order.getStatus());
        event.setTotalAmount(order.getTotalAmount());

        publishEvent(event, "OrderDelivered");
    }

    /**
     * Publish payment status changed event
     */
    @Async
    public void publishPaymentStatusChanged(Order order, String paymentStatus) {
        log.info("Publishing PAYMENT_STATUS_CHANGED event for order: {}", order.getOrderNumber());

        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(OrderEvent.OrderEventType.PAYMENT_STATUS_CHANGED);
        event.setTimestamp(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setCustomerId(order.getCustomerId());
        event.setRestaurantId(order.getRestaurantId());
        event.setStatus(order.getStatus());
        event.setPaymentStatus(paymentStatus);
        event.setTotalAmount(order.getTotalAmount());

        publishEvent(event, "PaymentStatusChanged");
    }

    /**
     * Publish event to SNS topic
     */
    private void publishEvent(OrderEvent event, String messageGroupId) {
        try {
            String message = objectMapper.writeValueAsString(event);

            PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(orderEventsTopicArn)
                .message(message)
                .subject("Order Event: " + event.getEventType())
                .messageAttributes(java.util.Map.of(
                    "eventType", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(event.getEventType().toString())
                        .build(),
                    "orderId", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("Number")
                        .stringValue(event.getOrderId().toString())
                        .build(),
                    "customerId", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("Number")
                        .stringValue(event.getCustomerId().toString())
                        .build()
                ))
                .build();

            PublishResponse response = snsClient.publish(publishRequest);
            log.info("Event published successfully. MessageId: {}, EventType: {}",
                response.messageId(), event.getEventType());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: {}", event, e);
        } catch (Exception e) {
            log.error("Failed to publish event to SNS: {}", event, e);
        }
    }
}

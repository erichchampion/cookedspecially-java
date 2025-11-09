package com.cookedspecially.notificationservice.consumer;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationPriority;
import com.cookedspecially.notificationservice.domain.NotificationType;
import com.cookedspecially.notificationservice.dto.SendNotificationRequest;
import com.cookedspecially.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order Event Consumer
 * Consumes order events from SQS and sends notifications
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final SqsClient sqsClient;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.order-events-queue-url:}")
    private String queueUrl;

    @Value("${aws.sqs.max-messages:10}")
    private int maxMessages;

    @Value("${aws.sqs.wait-time-seconds:20}")
    private int waitTimeSeconds;

    /**
     * Poll for order events every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void pollOrderEvents() {
        if (queueUrl == null || queueUrl.isEmpty()) {
            log.debug("Order events queue URL not configured, skipping polling");
            return;
        }

        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .messageAttributeNames("All")
                .build();

            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = response.messages();

            if (messages.isEmpty()) {
                log.debug("No order events to process");
                return;
            }

            log.info("Processing {} order events", messages.size());

            for (Message message : messages) {
                try {
                    processOrderEvent(message);
                    deleteMessage(message);
                } catch (Exception e) {
                    log.error("Failed to process order event: {}", e.getMessage(), e);
                    // Message will be retried after visibility timeout
                }
            }

        } catch (SqsException e) {
            log.error("Failed to poll order events: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Unexpected error polling order events", e);
        }
    }

    /**
     * Process individual order event
     */
    private void processOrderEvent(Message message) throws Exception {
        String body = message.body();
        log.debug("Processing order event: {}", body);

        // Parse SNS message wrapper
        JsonNode snsMessage = objectMapper.readTree(body);
        String eventJson = snsMessage.get("Message").asText();

        // Parse order event
        JsonNode event = objectMapper.readTree(eventJson);
        String eventType = event.get("eventType").asText();
        Long customerId = event.get("customerId").asLong();
        Long orderId = event.get("orderId").asLong();
        String orderNumber = event.get("orderNumber").asText();

        log.info("Processing {} event for order {}", eventType, orderNumber);

        switch (eventType) {
            case "ORDER_PLACED" -> sendOrderPlacedNotification(customerId, orderId, orderNumber, event);
            case "ORDER_CONFIRMED" -> sendOrderConfirmedNotification(customerId, orderId, orderNumber, event);
            case "ORDER_PREPARING" -> sendOrderPreparingNotification(customerId, orderId, orderNumber, event);
            case "ORDER_OUT_FOR_DELIVERY" -> sendOrderOutForDeliveryNotification(customerId, orderId, orderNumber, event);
            case "ORDER_DELIVERED" -> sendOrderDeliveredNotification(customerId, orderId, orderNumber, event);
            case "ORDER_CANCELLED" -> sendOrderCancelledNotification(customerId, orderId, orderNumber, event);
            default -> log.warn("Unknown order event type: {}", eventType);
        }
    }

    /**
     * Send ORDER_PLACED notification
     */
    private void sendOrderPlacedNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("restaurantName", event.get("restaurantName").asText());
        variables.put("totalAmount", event.get("totalAmount").asText());

        // Send email notification
        sendNotification(customerId, NotificationType.ORDER_PLACED, NotificationChannel.EMAIL,
            "Order Placed Successfully",
            String.format("Your order #%s has been placed successfully.", orderNumber),
            variables, orderId);

        // Send push notification
        sendNotification(customerId, NotificationType.ORDER_PLACED, NotificationChannel.PUSH,
            "Order Placed",
            String.format("Your order #%s is confirmed!", orderNumber),
            variables, orderId);
    }

    /**
     * Send ORDER_CONFIRMED notification
     */
    private void sendOrderConfirmedNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("restaurantName", event.get("restaurantName").asText());
        variables.put("estimatedDeliveryTime", event.has("estimatedDeliveryTime") ?
            event.get("estimatedDeliveryTime").asText() : "30-45 minutes");

        sendNotification(customerId, NotificationType.ORDER_CONFIRMED, NotificationChannel.EMAIL,
            "Order Confirmed",
            String.format("Your order #%s has been confirmed by the restaurant.", orderNumber),
            variables, orderId);

        sendNotification(customerId, NotificationType.ORDER_CONFIRMED, NotificationChannel.PUSH,
            "Order Confirmed",
            String.format("Order #%s confirmed! Estimated delivery: %s",
                orderNumber, variables.get("estimatedDeliveryTime")),
            variables, orderId);
    }

    /**
     * Send ORDER_PREPARING notification
     */
    private void sendOrderPreparingNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("restaurantName", event.get("restaurantName").asText());

        sendNotification(customerId, NotificationType.ORDER_PREPARING, NotificationChannel.PUSH,
            "Order Preparing",
            String.format("Your order #%s is being prepared.", orderNumber),
            variables, orderId);
    }

    /**
     * Send ORDER_OUT_FOR_DELIVERY notification
     */
    private void sendOrderOutForDeliveryNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("driverName", event.has("driverName") ? event.get("driverName").asText() : "Driver");

        sendNotification(customerId, NotificationType.ORDER_OUT_FOR_DELIVERY, NotificationChannel.PUSH,
            "Order Out for Delivery",
            String.format("Your order #%s is on the way!", orderNumber),
            variables, orderId);

        sendNotification(customerId, NotificationType.ORDER_OUT_FOR_DELIVERY, NotificationChannel.SMS,
            "Order Out for Delivery",
            String.format("Your order #%s is on the way!", orderNumber),
            variables, orderId);
    }

    /**
     * Send ORDER_DELIVERED notification
     */
    private void sendOrderDeliveredNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);

        sendNotification(customerId, NotificationType.ORDER_DELIVERED, NotificationChannel.PUSH,
            "Order Delivered",
            String.format("Your order #%s has been delivered. Enjoy your meal!", orderNumber),
            variables, orderId);

        sendNotification(customerId, NotificationType.ORDER_DELIVERED, NotificationChannel.EMAIL,
            "Order Delivered",
            String.format("Your order #%s has been delivered. Thank you for ordering with us!", orderNumber),
            variables, orderId);
    }

    /**
     * Send ORDER_CANCELLED notification
     */
    private void sendOrderCancelledNotification(Long customerId, Long orderId, String orderNumber, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("reason", event.has("cancellationReason") ?
            event.get("cancellationReason").asText() : "Order cancelled");

        sendNotification(customerId, NotificationType.ORDER_CANCELLED, NotificationChannel.EMAIL,
            "Order Cancelled",
            String.format("Your order #%s has been cancelled.", orderNumber),
            variables, orderId);

        sendNotification(customerId, NotificationType.ORDER_CANCELLED, NotificationChannel.PUSH,
            "Order Cancelled",
            String.format("Order #%s cancelled", orderNumber),
            variables, orderId);
    }

    /**
     * Send notification via NotificationService
     */
    private void sendNotification(Long userId, NotificationType type, NotificationChannel channel,
                                   String subject, String content, Map<String, Object> variables, Long orderId) {
        try {
            SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .type(type)
                .channel(channel)
                .priority(NotificationPriority.HIGH)
                .subject(subject)
                .content(content)
                .templateVariables(variables)
                .relatedEntityType("ORDER")
                .relatedEntityId(orderId)
                .build();

            notificationService.sendNotification(request);

        } catch (Exception e) {
            log.error("Failed to send {} notification to user {}: {}",
                type, userId, e.getMessage());
        }
    }

    /**
     * Delete processed message from queue
     */
    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();

            sqsClient.deleteMessage(deleteRequest);
            log.debug("Deleted message from queue");

        } catch (SqsException e) {
            log.error("Failed to delete message: {}", e.awsErrorDetails().errorMessage());
        }
    }
}

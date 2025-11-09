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
 * Restaurant Event Consumer
 * Consumes restaurant events from SQS and sends notifications
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final SqsClient sqsClient;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.restaurant-events-queue-url:}")
    private String queueUrl;

    @Value("${aws.sqs.max-messages:10}")
    private int maxMessages;

    @Value("${aws.sqs.wait-time-seconds:20}")
    private int waitTimeSeconds;

    /**
     * Poll for restaurant events every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void pollRestaurantEvents() {
        if (queueUrl == null || queueUrl.isEmpty()) {
            log.debug("Restaurant events queue URL not configured, skipping polling");
            return;
        }

        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .build();

            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = response.messages();

            if (!messages.isEmpty()) {
                log.info("Processing {} restaurant events", messages.size());

                for (Message message : messages) {
                    try {
                        processRestaurantEvent(message);
                        deleteMessage(message);
                    } catch (Exception e) {
                        log.error("Failed to process restaurant event: {}", e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error polling restaurant events", e);
        }
    }

    /**
     * Process individual restaurant event
     */
    private void processRestaurantEvent(Message message) throws Exception {
        JsonNode snsMessage = objectMapper.readTree(message.body());
        String eventJson = snsMessage.get("Message").asText();
        JsonNode event = objectMapper.readTree(eventJson);

        String eventType = event.get("eventType").asText();
        Long restaurantId = event.get("restaurantId").asLong();
        Long ownerId = event.get("ownerId").asLong();
        String restaurantName = event.get("restaurantName").asText();

        log.info("Processing {} event for restaurant {}", eventType, restaurantName);

        switch (eventType) {
            case "RESTAURANT_APPROVED" -> sendRestaurantApprovedNotification(ownerId, restaurantId, restaurantName);
            case "RESTAURANT_SUSPENDED" -> sendRestaurantSuspendedNotification(ownerId, restaurantId, restaurantName);
            case "RESTAURANT_REOPENED" -> sendRestaurantReopenedNotification(ownerId, restaurantId, restaurantName);
            case "MENU_UPDATED" -> sendMenuUpdatedNotification(ownerId, restaurantId, restaurantName);
            case "RATING_UPDATED" -> sendRatingUpdatedNotification(ownerId, restaurantId, restaurantName, event);
            default -> log.debug("Ignoring restaurant event type: {}", eventType);
        }
    }

    /**
     * Send RESTAURANT_APPROVED notification
     */
    private void sendRestaurantApprovedNotification(Long ownerId, Long restaurantId, String restaurantName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);

        sendNotification(ownerId, NotificationType.RESTAURANT_APPROVED, NotificationChannel.EMAIL,
            "Restaurant Approved!",
            String.format("Congratulations! Your restaurant '%s' has been approved and is now live.", restaurantName),
            variables, restaurantId);

        sendNotification(ownerId, NotificationType.RESTAURANT_APPROVED, NotificationChannel.PUSH,
            "Restaurant Approved",
            String.format("%s is now live!", restaurantName),
            variables, restaurantId);
    }

    /**
     * Send RESTAURANT_SUSPENDED notification
     */
    private void sendRestaurantSuspendedNotification(Long ownerId, Long restaurantId, String restaurantName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);

        sendNotification(ownerId, NotificationType.RESTAURANT_SUSPENDED, NotificationChannel.EMAIL,
            "Restaurant Suspended",
            String.format("Your restaurant '%s' has been suspended. Please contact support.", restaurantName),
            variables, restaurantId);

        sendNotification(ownerId, NotificationType.RESTAURANT_SUSPENDED, NotificationChannel.PUSH,
            "Restaurant Suspended",
            String.format("%s has been suspended", restaurantName),
            variables, restaurantId);
    }

    /**
     * Send RESTAURANT_REOPENED notification
     */
    private void sendRestaurantReopenedNotification(Long ownerId, Long restaurantId, String restaurantName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);

        sendNotification(ownerId, NotificationType.RESTAURANT_REOPENED, NotificationChannel.EMAIL,
            "Restaurant Reopened",
            String.format("Your restaurant '%s' is now accepting orders again.", restaurantName),
            variables, restaurantId);
    }

    /**
     * Send MENU_UPDATED notification (low priority)
     */
    private void sendMenuUpdatedNotification(Long ownerId, Long restaurantId, String restaurantName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);

        SendNotificationRequest request = SendNotificationRequest.builder()
            .userId(ownerId)
            .type(NotificationType.MENU_UPDATED)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.LOW)
            .subject("Menu Updated")
            .content(String.format("Menu for %s has been updated successfully.", restaurantName))
            .templateVariables(variables)
            .relatedEntityType("RESTAURANT")
            .relatedEntityId(restaurantId)
            .build();

        try {
            notificationService.sendNotification(request);
        } catch (Exception e) {
            log.error("Failed to send MENU_UPDATED notification: {}", e.getMessage());
        }
    }

    /**
     * Send RATING_UPDATED notification
     */
    private void sendRatingUpdatedNotification(Long ownerId, Long restaurantId, String restaurantName, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);
        variables.put("rating", event.has("rating") ? event.get("rating").asText() : "N/A");

        sendNotification(ownerId, NotificationType.NEW_REVIEW, NotificationChannel.IN_APP,
            "New Review",
            String.format("Your restaurant '%s' has received a new review.", restaurantName),
            variables, restaurantId);
    }

    /**
     * Send notification via NotificationService
     */
    private void sendNotification(Long userId, NotificationType type, NotificationChannel channel,
                                   String subject, String content, Map<String, Object> variables, Long restaurantId) {
        try {
            SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .type(type)
                .channel(channel)
                .priority(NotificationPriority.MEDIUM)
                .subject(subject)
                .content(content)
                .templateVariables(variables)
                .relatedEntityType("RESTAURANT")
                .relatedEntityId(restaurantId)
                .build();

            notificationService.sendNotification(request);

        } catch (Exception e) {
            log.error("Failed to send {} notification to user {}: {}", type, userId, e.getMessage());
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

        } catch (SqsException e) {
            log.error("Failed to delete message: {}", e.awsErrorDetails().errorMessage());
        }
    }
}

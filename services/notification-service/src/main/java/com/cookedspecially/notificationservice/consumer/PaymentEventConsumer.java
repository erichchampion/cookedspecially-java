package com.cookedspecially.notificationservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationPriority;
import com.cookedspecially.notificationservice.domain.NotificationType;
import com.cookedspecially.notificationservice.dto.SendNotificationRequest;
import com.cookedspecially.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payment Event Consumer
 * Consumes payment events from SQS and sends notifications
 */
@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final SqsClient sqsClient;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // Constructor
    public PaymentEventConsumer(SqsClient sqsClient,
                 NotificationService notificationService,
                 ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Value("${aws.sqs.payment-events-queue-url:}")
    private String queueUrl;

    @Value("${aws.sqs.max-messages:10}")
    private int maxMessages;

    @Value("${aws.sqs.wait-time-seconds:20}")
    private int waitTimeSeconds;

    /**
     * Poll for payment events every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void pollPaymentEvents() {
        if (queueUrl == null || queueUrl.isEmpty()) {
            log.debug("Payment events queue URL not configured, skipping polling");
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
                log.info("Processing {} payment events", messages.size());

                for (Message message : messages) {
                    try {
                        processPaymentEvent(message);
                        deleteMessage(message);
                    } catch (Exception e) {
                        log.error("Failed to process payment event: {}", e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error polling payment events", e);
        }
    }

    /**
     * Process individual payment event
     */
    private void processPaymentEvent(Message message) throws Exception {
        JsonNode snsMessage = objectMapper.readTree(message.body());
        String eventJson = snsMessage.get("Message").asText();
        JsonNode event = objectMapper.readTree(eventJson);

        String eventType = event.get("eventType").asText();
        Long customerId = event.get("customerId").asLong();
        Long paymentId = event.get("paymentId").asLong();

        log.info("Processing {} event for payment {}", eventType, paymentId);

        switch (eventType) {
            case "PAYMENT_RECEIVED" -> sendPaymentReceivedNotification(customerId, paymentId, event);
            case "PAYMENT_FAILED" -> sendPaymentFailedNotification(customerId, paymentId, event);
            case "REFUND_PROCESSED" -> sendRefundProcessedNotification(customerId, paymentId, event);
            default -> log.warn("Unknown payment event type: {}", eventType);
        }
    }

    /**
     * Send PAYMENT_RECEIVED notification
     */
    private void sendPaymentReceivedNotification(Long customerId, Long paymentId, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", event.get("amount").asText());
        variables.put("orderNumber", event.has("orderNumber") ? event.get("orderNumber").asText() : "N/A");
        variables.put("paymentMethod", event.get("paymentMethod").asText());

        sendNotification(customerId, NotificationType.PAYMENT_RECEIVED, NotificationChannel.EMAIL,
            "Payment Received",
            String.format("We've received your payment of $%s.", variables.get("amount")),
            variables, paymentId);
    }

    /**
     * Send PAYMENT_FAILED notification
     */
    private void sendPaymentFailedNotification(Long customerId, Long paymentId, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", event.get("amount").asText());
        variables.put("reason", event.has("failureReason") ? event.get("failureReason").asText() : "Payment declined");

        sendNotification(customerId, NotificationType.PAYMENT_FAILED, NotificationChannel.EMAIL,
            "Payment Failed",
            String.format("Your payment of $%s failed. Please try again.", variables.get("amount")),
            variables, paymentId);

        sendNotification(customerId, NotificationType.PAYMENT_FAILED, NotificationChannel.PUSH,
            "Payment Failed",
            "Your payment failed. Please update your payment method.",
            variables, paymentId);
    }

    /**
     * Send REFUND_PROCESSED notification
     */
    private void sendRefundProcessedNotification(Long customerId, Long paymentId, JsonNode event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", event.get("amount").asText());
        variables.put("refundId", event.get("refundId").asText());

        sendNotification(customerId, NotificationType.REFUND_PROCESSED, NotificationChannel.EMAIL,
            "Refund Processed",
            String.format("Your refund of $%s has been processed.", variables.get("amount")),
            variables, paymentId);
    }

    /**
     * Send notification via NotificationService
     */
    private void sendNotification(Long userId, NotificationType type, NotificationChannel channel,
                                   String subject, String content, Map<String, Object> variables, Long paymentId) {
        try {
            SendNotificationRequest request = new SendNotificationRequest(
                userId,
                type,
                channel,
                NotificationPriority.HIGH,
                subject,
                content,
                null, // recipient
                null, // templateId
                variables,
                "PAYMENT",
                paymentId
            );

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

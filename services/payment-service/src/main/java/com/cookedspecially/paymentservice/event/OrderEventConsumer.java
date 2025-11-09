package com.cookedspecially.paymentservice.event;

import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.dto.CreatePaymentRequest;
import com.cookedspecially.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order Event Consumer
 *
 * Consumes ORDER_CONFIRMED events from SQS queue to trigger payment processing
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @Value("${aws.sqs.order-events-queue-url}")
    private String orderEventsQueueUrl;

    @Value("${aws.sqs.enabled:false}")
    private boolean sqsEnabled;

    /**
     * Poll SQS queue for order events
     * Runs every 5 seconds when SQS is enabled
     */
    @Scheduled(fixedDelay = 5000)
    public void pollOrderEvents() {
        if (!sqsEnabled || orderEventsQueueUrl == null || orderEventsQueueUrl.isEmpty()) {
            return;
        }

        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(orderEventsQueueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .messageAttributeNames("All")
                .build();

            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = response.messages();

            if (!messages.isEmpty()) {
                log.info("Received {} order event messages from SQS", messages.size());
            }

            for (Message message : messages) {
                try {
                    processOrderEvent(message);

                    // Delete message from queue after successful processing
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(orderEventsQueueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                    sqsClient.deleteMessage(deleteRequest);

                } catch (Exception e) {
                    log.error("Failed to process order event message: {}", message.messageId(), e);
                    // Message will remain in queue and be retried
                }
            }
        } catch (Exception e) {
            log.error("Failed to poll SQS queue: {}", orderEventsQueueUrl, e);
        }
    }

    /**
     * Process individual order event
     */
    private void processOrderEvent(Message message) throws Exception {
        String body = message.body();
        log.debug("Processing order event: {}", body);

        // Parse SNS message (SQS receives SNS notifications)
        JsonNode snsMessage = objectMapper.readTree(body);
        String eventMessage = snsMessage.get("Message").asText();
        JsonNode orderEvent = objectMapper.readTree(eventMessage);

        String eventType = orderEvent.get("event_type").asText();

        // Only process ORDER_CONFIRMED events
        if ("ORDER_CONFIRMED".equals(eventType)) {
            Long orderId = orderEvent.get("order_id").asLong();
            Long customerId = orderEvent.get("customer_id").asLong();
            BigDecimal totalAmount = new BigDecimal(orderEvent.get("total_amount").asText());

            log.info("Processing ORDER_CONFIRMED event for order: {}", orderId);

            // Create payment request
            CreatePaymentRequest paymentRequest = CreatePaymentRequest.builder()
                .orderId(orderId)
                .customerId(customerId)
                .amount(totalAmount)
                .currency("USD")
                .provider(PaymentProvider.STRIPE) // Default to Stripe, could be configurable
                .description("Payment for order " + orderId)
                .build();

            // Process payment
            paymentService.processPayment(paymentRequest);

            log.info("Payment initiated for order: {}", orderId);
        }
    }
}

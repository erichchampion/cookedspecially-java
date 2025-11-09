package com.cookedspecially.paymentservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.dto.CreatePaymentRequest;
import com.cookedspecially.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final SqsClient sqsClient;

    // Constructor
    public OrderEventConsumer(SqsClient sqsClient, ObjectMapper objectMapper, PaymentService paymentService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }
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
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest(
                orderId,
                customerId,
                totalAmount,
                "USD",
                PaymentProvider.STRIPE, // Default to Stripe, could be configurable
                null, // paymentMethodId
                "Payment for order " + orderId,
                null // metadata
            );

            // Process payment
            paymentService.processPayment(paymentRequest);

            log.info("Payment initiated for order: {}", orderId);
        }
    }
}

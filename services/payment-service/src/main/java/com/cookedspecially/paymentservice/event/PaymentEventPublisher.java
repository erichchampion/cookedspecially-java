package com.cookedspecially.paymentservice.event;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.Refund;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment Event Publisher
 *
 * Publishes payment lifecycle events to AWS SNS topics
 */
@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    public PaymentEventPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    @Value("${aws.sns.payment-events-topic-arn}")
    private String paymentEventsTopicArn;

    /**
     * Publish payment completed event
     */
    @Async
    public void publishPaymentCompleted(Payment payment) {
        log.info("Publishing PAYMENT_COMPLETED event for payment: {}", payment.getPaymentNumber());

                PaymentEvent event = new PaymentEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(PaymentEvent.PaymentEventType.PAYMENT_COMPLETED);
        event.setTimestamp(LocalDateTime.now());
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setCustomerId(payment.getCustomerId());
        event.setStatus(payment.getStatus());
        event.setProvider(payment.getProvider());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());

        publishEvent(event, "PaymentCompleted");
    }

    /**
     * Publish payment failed event
     */
    @Async
    public void publishPaymentFailed(Payment payment) {
        log.info("Publishing PAYMENT_FAILED event for payment: {}", payment.getPaymentNumber());

                PaymentEvent event = new PaymentEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(PaymentEvent.PaymentEventType.PAYMENT_FAILED);
        event.setTimestamp(LocalDateTime.now());
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setCustomerId(payment.getCustomerId());
        event.setStatus(payment.getStatus());
        event.setProvider(payment.getProvider());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());
        event.setFailureReason(payment.getFailureReason());

        publishEvent(event, "PaymentFailed");
    }

    /**
     * Publish payment cancelled event
     */
    @Async
    public void publishPaymentCancelled(Payment payment) {
        log.info("Publishing PAYMENT_CANCELLED event for payment: {}", payment.getPaymentNumber());

                PaymentEvent event = new PaymentEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(PaymentEvent.PaymentEventType.PAYMENT_CANCELLED);
        event.setTimestamp(LocalDateTime.now());
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setCustomerId(payment.getCustomerId());
        event.setStatus(payment.getStatus());
        event.setProvider(payment.getProvider());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());

        publishEvent(event, "PaymentCancelled");
    }

    /**
     * Publish refund completed event
     */
    @Async
    public void publishRefundCompleted(Refund refund, Payment payment) {
        log.info("Publishing REFUND_COMPLETED event for refund: {}", refund.getRefundNumber());

                PaymentEvent event = new PaymentEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(PaymentEvent.PaymentEventType.REFUND_COMPLETED);
        event.setTimestamp(LocalDateTime.now());
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setCustomerId(payment.getCustomerId());
        event.setStatus(payment.getStatus());
        event.setProvider(payment.getProvider());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());
        event.setRefundAmount(refund.getAmount());

        publishEvent(event, "RefundCompleted");
    }

    /**
     * Publish refund failed event
     */
    @Async
    public void publishRefundFailed(Refund refund, Payment payment) {
        log.info("Publishing REFUND_FAILED event for refund: {}", refund.getRefundNumber());

                PaymentEvent event = new PaymentEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(PaymentEvent.PaymentEventType.REFUND_FAILED);
        event.setTimestamp(LocalDateTime.now());
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setCustomerId(payment.getCustomerId());
        event.setStatus(payment.getStatus());
        event.setProvider(payment.getProvider());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());
        event.setRefundAmount(refund.getAmount());
        event.setFailureReason(refund.getFailureReason());

        publishEvent(event, "RefundFailed");
    }

    /**
     * Publish event to SNS topic
     */
    private void publishEvent(PaymentEvent event, String messageGroupId) {
        try {
            String message = objectMapper.writeValueAsString(event);

            PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(paymentEventsTopicArn)
                .message(message)
                .subject("Payment Event: " + event.getEventType())
                .messageAttributes(java.util.Map.of(
                    "eventType", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(event.getEventType().toString())
                        .build(),
                    "paymentId", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("Number")
                        .stringValue(event.getPaymentId().toString())
                        .build(),
                    "orderId", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("Number")
                        .stringValue(event.getOrderId().toString())
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

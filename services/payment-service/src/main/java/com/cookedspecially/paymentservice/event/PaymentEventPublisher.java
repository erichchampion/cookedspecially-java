package com.cookedspecially.paymentservice.event;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.Refund;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.payment-events-topic-arn}")
    private String paymentEventsTopicArn;

    /**
     * Publish payment completed event
     */
    @Async
    public void publishPaymentCompleted(Payment payment) {
        log.info("Publishing PAYMENT_COMPLETED event for payment: {}", payment.getPaymentNumber());

        PaymentEvent event = PaymentEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(PaymentEvent.PaymentEventType.PAYMENT_COMPLETED)
            .timestamp(LocalDateTime.now())
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .build();

        publishEvent(event, "PaymentCompleted");
    }

    /**
     * Publish payment failed event
     */
    @Async
    public void publishPaymentFailed(Payment payment) {
        log.info("Publishing PAYMENT_FAILED event for payment: {}", payment.getPaymentNumber());

        PaymentEvent event = PaymentEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(PaymentEvent.PaymentEventType.PAYMENT_FAILED)
            .timestamp(LocalDateTime.now())
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .failureReason(payment.getFailureReason())
            .build();

        publishEvent(event, "PaymentFailed");
    }

    /**
     * Publish payment cancelled event
     */
    @Async
    public void publishPaymentCancelled(Payment payment) {
        log.info("Publishing PAYMENT_CANCELLED event for payment: {}", payment.getPaymentNumber());

        PaymentEvent event = PaymentEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(PaymentEvent.PaymentEventType.PAYMENT_CANCELLED)
            .timestamp(LocalDateTime.now())
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .build();

        publishEvent(event, "PaymentCancelled");
    }

    /**
     * Publish refund completed event
     */
    @Async
    public void publishRefundCompleted(Refund refund, Payment payment) {
        log.info("Publishing REFUND_COMPLETED event for refund: {}", refund.getRefundNumber());

        PaymentEvent event = PaymentEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(PaymentEvent.PaymentEventType.REFUND_COMPLETED)
            .timestamp(LocalDateTime.now())
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .refundAmount(refund.getAmount())
            .build();

        publishEvent(event, "RefundCompleted");
    }

    /**
     * Publish refund failed event
     */
    @Async
    public void publishRefundFailed(Refund refund, Payment payment) {
        log.info("Publishing REFUND_FAILED event for refund: {}", refund.getRefundNumber());

        PaymentEvent event = PaymentEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(PaymentEvent.PaymentEventType.REFUND_FAILED)
            .timestamp(LocalDateTime.now())
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .refundAmount(refund.getAmount())
            .failureReason(refund.getFailureReason())
            .build();

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

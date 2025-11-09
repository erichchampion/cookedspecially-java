package com.cookedspecially.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.notificationservice.exception.NotificationSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SMS Service using AWS SNS
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final SnsClient snsClient;

    // Constructor
    public SmsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    @Value("${aws.sns.sms-sender-id:CookedSpec}")
    private String smsSenderId;

    /**
     * Send SMS message
     */
    public String sendSms(String phoneNumber, String message) {
        log.info("Sending SMS to: {}", maskPhoneNumber(phoneNumber));

        try {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();

            // Set SMS type to Transactional (higher reliability and deliverability)
            smsAttributes.put("AWS.SNS.SMS.SMSType",
                MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("Transactional")
                    .build());

            // Set sender ID (name shown to recipient)
            smsAttributes.put("AWS.SNS.SMS.SenderID",
                MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(smsSenderId)
                    .build());

            PublishRequest request = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .messageAttributes(smsAttributes)
                .build();

            PublishResponse response = snsClient.publish(request);
            String messageId = response.messageId();

            log.info("SMS sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (SnsException e) {
            log.error("Failed to send SMS to {}: {}", maskPhoneNumber(phoneNumber),
                e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("SMS", e);
        } catch (Exception e) {
            log.error("Unexpected error sending SMS to {}", maskPhoneNumber(phoneNumber), e);
            throw new NotificationSendException("SMS", e);
        }
    }

    /**
     * Send SMS with custom attributes
     */
    public String sendSmsWithAttributes(String phoneNumber, String message, Map<String, String> attributes) {
        log.info("Sending SMS with attributes to: {}", maskPhoneNumber(phoneNumber));

        try {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();

            // Set default SMS type
            smsAttributes.put("AWS.SNS.SMS.SMSType",
                MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("Transactional")
                    .build());

            // Set sender ID
            smsAttributes.put("AWS.SNS.SMS.SenderID",
                MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(smsSenderId)
                    .build());

            // Add custom attributes
            if (attributes != null) {
                attributes.forEach((key, value) ->
                    smsAttributes.put(key,
                        MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(value)
                            .build())
                );
            }

            PublishRequest request = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .messageAttributes(smsAttributes)
                .build();

            PublishResponse response = snsClient.publish(request);
            String messageId = response.messageId();

            log.info("SMS with attributes sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (SnsException e) {
            log.error("Failed to send SMS to {}: {}", maskPhoneNumber(phoneNumber),
                e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("SMS", e);
        } catch (Exception e) {
            log.error("Unexpected error sending SMS to {}", maskPhoneNumber(phoneNumber), e);
            throw new NotificationSendException("SMS", e);
        }
    }

    /**
     * Check if phone number is opted out
     */
    public boolean isPhoneNumberOptedOut(String phoneNumber) {
        try {
            CheckIfPhoneNumberIsOptedOutRequest request =
                CheckIfPhoneNumberIsOptedOutRequest.builder()
                    .phoneNumber(phoneNumber)
                    .build();

            CheckIfPhoneNumberIsOptedOutResponse response =
                snsClient.checkIfPhoneNumberIsOptedOut(request);

            return response.isOptedOut();

        } catch (SnsException e) {
            log.error("Failed to check opt-out status for {}: {}",
                maskPhoneNumber(phoneNumber), e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    /**
     * Get SMS attributes for account
     */
    public Map<String, String> getSmsAttributes() {
        try {
            GetSmsAttributesResponse response = snsClient.getSMSAttributes(
                GetSmsAttributesRequest.builder().build()
            );

            return response.attributes();

        } catch (SnsException e) {
            log.error("Failed to get SMS attributes: {}", e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("SMS", e);
        }
    }

    /**
     * Set SMS attributes
     */
    public void setSmsAttributes(Map<String, String> attributes) {
        try {
            SetSmsAttributesRequest request = SetSmsAttributesRequest.builder()
                .attributes(attributes)
                .build();

            snsClient.setSMSAttributes(request);
            log.info("SMS attributes updated successfully");

        } catch (SnsException e) {
            log.error("Failed to set SMS attributes: {}", e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("SMS", e);
        }
    }

    /**
     * Mask phone number for logging (keep last 4 digits)
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        String last4 = phoneNumber.substring(phoneNumber.length() - 4);
        return "****" + last4;
    }
}

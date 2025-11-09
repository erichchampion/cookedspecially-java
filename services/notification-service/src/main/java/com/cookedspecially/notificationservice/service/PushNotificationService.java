package com.cookedspecially.notificationservice.service;

import com.cookedspecially.notificationservice.exception.NotificationSendException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Push Notification Service using AWS SNS
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PushNotificationService {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.push-platform-application-arn-android:}")
    private String androidPlatformArn;

    @Value("${aws.sns.push-platform-application-arn-ios:}")
    private String iosPlatformArn;

    /**
     * Send push notification to Android device
     */
    public String sendAndroidPush(String deviceToken, String title, String body, Map<String, String> data) {
        log.info("Sending Android push notification");

        if (androidPlatformArn == null || androidPlatformArn.isEmpty()) {
            log.warn("Android platform ARN not configured, skipping push notification");
            return null;
        }

        try {
            // Get or create endpoint for device token
            String endpointArn = getOrCreateEndpoint(deviceToken, androidPlatformArn);

            // Build FCM message
            Map<String, Object> fcmMessage = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);
            fcmMessage.put("notification", notification);

            if (data != null && !data.isEmpty()) {
                fcmMessage.put("data", data);
            }

            // Build SNS message
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("default", body);
            messageMap.put("GCM", objectMapper.writeValueAsString(fcmMessage));

            String message = objectMapper.writeValueAsString(messageMap);

            PublishRequest request = PublishRequest.builder()
                .targetArn(endpointArn)
                .message(message)
                .messageStructure("json")
                .build();

            PublishResponse response = snsClient.publish(request);
            String messageId = response.messageId();

            log.info("Android push notification sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (Exception e) {
            log.error("Failed to send Android push notification", e);
            throw new NotificationSendException("PUSH_ANDROID", e);
        }
    }

    /**
     * Send push notification to iOS device
     */
    public String sendIosPush(String deviceToken, String title, String body, Map<String, String> data) {
        log.info("Sending iOS push notification");

        if (iosPlatformArn == null || iosPlatformArn.isEmpty()) {
            log.warn("iOS platform ARN not configured, skipping push notification");
            return null;
        }

        try {
            // Get or create endpoint for device token
            String endpointArn = getOrCreateEndpoint(deviceToken, iosPlatformArn);

            // Build APNS message
            Map<String, Object> apnsMessage = new HashMap<>();
            Map<String, Object> aps = new HashMap<>();
            Map<String, Object> alert = new HashMap<>();
            alert.put("title", title);
            alert.put("body", body);
            aps.put("alert", alert);
            aps.put("sound", "default");
            apnsMessage.put("aps", aps);

            if (data != null && !data.isEmpty()) {
                apnsMessage.putAll(data);
            }

            // Build SNS message
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("default", body);
            messageMap.put("APNS", objectMapper.writeValueAsString(apnsMessage));
            messageMap.put("APNS_SANDBOX", objectMapper.writeValueAsString(apnsMessage));

            String message = objectMapper.writeValueAsString(messageMap);

            PublishRequest request = PublishRequest.builder()
                .targetArn(endpointArn)
                .message(message)
                .messageStructure("json")
                .build();

            PublishResponse response = snsClient.publish(request);
            String messageId = response.messageId();

            log.info("iOS push notification sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (Exception e) {
            log.error("Failed to send iOS push notification", e);
            throw new NotificationSendException("PUSH_IOS", e);
        }
    }

    /**
     * Get or create platform endpoint for device token
     */
    private String getOrCreateEndpoint(String deviceToken, String platformArn) {
        try {
            // Try to create endpoint
            CreatePlatformEndpointRequest createRequest = CreatePlatformEndpointRequest.builder()
                .platformApplicationArn(platformArn)
                .token(deviceToken)
                .build();

            CreatePlatformEndpointResponse createResponse = snsClient.createPlatformEndpoint(createRequest);
            return createResponse.endpointArn();

        } catch (InvalidParameterException e) {
            // Endpoint might already exist, extract ARN from error message
            String message = e.awsErrorDetails().errorMessage();
            if (message.contains("Endpoint") && message.contains("already exists")) {
                // Extract ARN from error message
                int start = message.indexOf("arn:");
                if (start >= 0) {
                    int end = message.indexOf(" ", start);
                    if (end < 0) end = message.length();
                    return message.substring(start, end);
                }
            }
            throw new NotificationSendException("PUSH", e);

        } catch (SnsException e) {
            log.error("Failed to get or create endpoint: {}", e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("PUSH", e);
        }
    }

    /**
     * Delete platform endpoint
     */
    public void deleteEndpoint(String endpointArn) {
        try {
            DeleteEndpointRequest request = DeleteEndpointRequest.builder()
                .endpointArn(endpointArn)
                .build();

            snsClient.deleteEndpoint(request);
            log.info("Deleted endpoint: {}", endpointArn);

        } catch (SnsException e) {
            log.error("Failed to delete endpoint {}: {}", endpointArn, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("PUSH", e);
        }
    }

    /**
     * Get endpoint attributes
     */
    public Map<String, String> getEndpointAttributes(String endpointArn) {
        try {
            GetEndpointAttributesRequest request = GetEndpointAttributesRequest.builder()
                .endpointArn(endpointArn)
                .build();

            GetEndpointAttributesResponse response = snsClient.getEndpointAttributes(request);
            return response.attributes();

        } catch (SnsException e) {
            log.error("Failed to get endpoint attributes for {}: {}",
                endpointArn, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("PUSH", e);
        }
    }

    /**
     * Check if endpoint is enabled
     */
    public boolean isEndpointEnabled(String endpointArn) {
        try {
            Map<String, String> attributes = getEndpointAttributes(endpointArn);
            return "true".equalsIgnoreCase(attributes.get("Enabled"));
        } catch (Exception e) {
            log.error("Failed to check endpoint status", e);
            return false;
        }
    }
}

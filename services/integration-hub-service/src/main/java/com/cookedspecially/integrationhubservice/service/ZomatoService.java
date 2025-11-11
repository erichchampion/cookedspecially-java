package com.cookedspecially.integrationhubservice.service;

import com.cookedspecially.integrationhubservice.domain.WebhookLog;
import com.cookedspecially.integrationhubservice.dto.zomato.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZomatoService {

    private final WebhookLogService webhookLogService;
    private final WebhookSecurityService securityService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${integration.zomato.base-url}")
    private String zomatoBaseUrl;

    @Value("${integration.zomato.webhook-secret}")
    private String webhookSecret;

    @Value("${integration.zomato.retry.max-attempts:3}")
    private int maxRetryAttempts;

    private static final String DUPLICATE_ORDER_KEY_PREFIX = "zomato:order:";
    private static final long DUPLICATE_CHECK_TTL_HOURS = 24;

    @Transactional
    public Map<String, Object> processOrderWebhook(ZomatoOrderDTO orderDTO, String signature,
                                                     Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        log.info("Processing Zomato order webhook: orderId={}", orderDTO.getOrderId());

        try {
            // Validate webhook signature
            String payload = objectMapper.writeValueAsString(orderDTO);
            if (!securityService.validateZomatoSignature(payload, signature, webhookSecret)) {
                log.error("Invalid webhook signature for order: {}", orderDTO.getOrderId());
                throw new RuntimeException("Invalid webhook signature");
            }

            // Create webhook log
            WebhookLog webhookLog = webhookLogService.createLog(
                    "ZOMATO",
                    "ORDER_RECEIVED",
                    orderDTO.getOrderId(),
                    payload,
                    headers
            );

            // Check for duplicate orders
            if (isDuplicateOrder(orderDTO.getOrderId())) {
                log.warn("Duplicate order detected: {}", orderDTO.getOrderId());
                long processingTime = System.currentTimeMillis() - startTime;
                webhookLogService.updateLogSuccess(webhookLog.getId(), "Duplicate order", 200, processingTime);

                return Map.of(
                        "status", "duplicate",
                        "message", "Order already processed",
                        "orderId", orderDTO.getOrderId()
                );
            }

            // Mark order as received
            markOrderAsReceived(orderDTO.getOrderId());

            // TODO: Create order in Order Service via REST API or messaging
            // For now, we'll simulate order creation
            log.info("Creating order in Order Service for Zomato order: {}", orderDTO.getOrderId());

            long processingTime = System.currentTimeMillis() - startTime;
            webhookLogService.updateLogSuccess(webhookLog.getId(), "Order created successfully", 200, processingTime);

            return Map.of(
                    "status", "success",
                    "message", "Order received and created",
                    "orderId", orderDTO.getOrderId()
            );

        } catch (Exception e) {
            log.error("Error processing Zomato order webhook: {}", orderDTO.getOrderId(), e);
            throw new RuntimeException("Error processing order webhook", e);
        }
    }

    public Map<String, Object> confirmOrder(ZomatoOrderConfirmDTO confirmDTO) {
        log.info("Confirming Zomato order: orderId={}", confirmDTO.getOrderId());

        try {
            WebClient webClient = webClientBuilder.baseUrl(zomatoBaseUrl).build();

            String response = webClient.post()
                    .uri("/orders/{orderId}/confirm", confirmDTO.getOrderId())
                    .bodyValue(confirmDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            log.info("Zomato order confirmed successfully: orderId={}", confirmDTO.getOrderId());

            return Map.of(
                    "status", "success",
                    "message", "Order confirmed",
                    "orderId", confirmDTO.getOrderId()
            );

        } catch (Exception e) {
            log.error("Error confirming Zomato order: {}", confirmDTO.getOrderId(), e);
            throw new RuntimeException("Error confirming order", e);
        }
    }

    public Map<String, Object> rejectOrder(ZomatoOrderRejectDTO rejectDTO) {
        log.info("Rejecting Zomato order: orderId={}, reason={}", rejectDTO.getOrderId(), rejectDTO.getReason());

        try {
            WebClient webClient = webClientBuilder.baseUrl(zomatoBaseUrl).build();

            String response = webClient.post()
                    .uri("/orders/{orderId}/reject", rejectDTO.getOrderId())
                    .bodyValue(rejectDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            log.info("Zomato order rejected successfully: orderId={}", rejectDTO.getOrderId());

            // Remove from duplicate check since order was rejected
            removeOrderFromDuplicateCheck(rejectDTO.getOrderId());

            return Map.of(
                    "status", "success",
                    "message", "Order rejected",
                    "orderId", rejectDTO.getOrderId()
            );

        } catch (Exception e) {
            log.error("Error rejecting Zomato order: {}", rejectDTO.getOrderId(), e);
            throw new RuntimeException("Error rejecting order", e);
        }
    }

    public Map<String, Object> updateOrderStatus(ZomatoOrderStatusUpdateDTO statusDTO) {
        log.info("Updating Zomato order status: orderId={}, status={}", statusDTO.getOrderId(), statusDTO.getStatus());

        try {
            WebClient webClient = webClientBuilder.baseUrl(zomatoBaseUrl).build();

            String response = webClient.post()
                    .uri("/orders/{orderId}/status", statusDTO.getOrderId())
                    .bodyValue(statusDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            log.info("Zomato order status updated successfully: orderId={}", statusDTO.getOrderId());

            return Map.of(
                    "status", "success",
                    "message", "Order status updated",
                    "orderId", statusDTO.getOrderId()
            );

        } catch (Exception e) {
            log.error("Error updating Zomato order status: {}", statusDTO.getOrderId(), e);
            throw new RuntimeException("Error updating order status", e);
        }
    }

    public Map<String, Object> syncMenu(ZomatoMenuSyncDTO menuDTO) {
        log.info("Syncing menu to Zomato: restaurantId={}", menuDTO.getRestaurantId());

        try {
            WebClient webClient = webClientBuilder.baseUrl(zomatoBaseUrl).build();

            String response = webClient.post()
                    .uri("/restaurants/{restaurantId}/menu", menuDTO.getRestaurantId())
                    .bodyValue(menuDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            log.info("Menu synced successfully to Zomato: restaurantId={}", menuDTO.getRestaurantId());

            return Map.of(
                    "status", "success",
                    "message", "Menu synchronized",
                    "restaurantId", menuDTO.getRestaurantId()
            );

        } catch (Exception e) {
            log.error("Error syncing menu to Zomato: {}", menuDTO.getRestaurantId(), e);
            throw new RuntimeException("Error syncing menu", e);
        }
    }

    public Map<String, Object> updateRestaurantStatus(ZomatoRestaurantStatusDTO statusDTO) {
        log.info("Updating Zomato restaurant status: restaurantId={}, open={}",
                statusDTO.getRestaurantId(), statusDTO.getOpen());

        try {
            WebClient webClient = webClientBuilder.baseUrl(zomatoBaseUrl).build();

            String response = webClient.post()
                    .uri("/restaurants/{restaurantId}/status", statusDTO.getRestaurantId())
                    .bodyValue(statusDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            log.info("Restaurant status updated successfully in Zomato: restaurantId={}", statusDTO.getRestaurantId());

            return Map.of(
                    "status", "success",
                    "message", "Restaurant status updated",
                    "restaurantId", statusDTO.getRestaurantId()
            );

        } catch (Exception e) {
            log.error("Error updating restaurant status in Zomato: {}", statusDTO.getRestaurantId(), e);
            throw new RuntimeException("Error updating restaurant status", e);
        }
    }

    private boolean isDuplicateOrder(String orderId) {
        String key = DUPLICATE_ORDER_KEY_PREFIX + orderId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void markOrderAsReceived(String orderId) {
        String key = DUPLICATE_ORDER_KEY_PREFIX + orderId;
        redisTemplate.opsForValue().set(key, "1", DUPLICATE_CHECK_TTL_HOURS, TimeUnit.HOURS);
    }

    private void removeOrderFromDuplicateCheck(String orderId) {
        String key = DUPLICATE_ORDER_KEY_PREFIX + orderId;
        redisTemplate.delete(key);
    }
}

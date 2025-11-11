package com.cookedspecially.integrationhubservice.controller;

import com.cookedspecially.integrationhubservice.domain.WebhookLog;
import com.cookedspecially.integrationhubservice.service.WebhookLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/integrations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Integration Health", description = "Integration monitoring and health check endpoints")
public class IntegrationHealthController {

    private final WebhookLogService webhookLogService;

    @GetMapping("/health")
    @Operation(summary = "Integration health status", description = "Get health status of all integrations")
    public ResponseEntity<Map<String, Object>> getIntegrationHealth() {
        log.info("Fetching integration health status");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("timestamp", LocalDateTime.now());
        health.put("integrations", Map.of(
                "zomato", Map.of("status", "active", "enabled", true),
                "facebook", Map.of("status", "active", "enabled", true)
        ));

        return ResponseEntity.ok(health);
    }

    @GetMapping("/webhooks/logs")
    @Operation(summary = "Get webhook logs", description = "Get webhook logs for a partner")
    public ResponseEntity<Page<WebhookLog>> getWebhookLogs(
            @RequestParam String partnerId,
            Pageable pageable) {

        log.info("Fetching webhook logs: partnerId={}", partnerId);
        Page<WebhookLog> logs = webhookLogService.getLogsByPartner(partnerId, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/webhooks/logs/date-range")
    @Operation(summary = "Get webhook logs by date range", description = "Get webhook logs filtered by date range")
    public ResponseEntity<Page<WebhookLog>> getWebhookLogsByDateRange(
            @RequestParam String partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {

        log.info("Fetching webhook logs by date range: partnerId={}, from={}, to={}",
                partnerId, startDate, endDate);
        Page<WebhookLog> logs = webhookLogService.getLogsByDateRange(partnerId, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Integration analytics", description = "Get analytics for integrations")
    public ResponseEntity<Map<String, Object>> getIntegrationAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        log.info("Fetching integration analytics: from={}, to={}", from, to);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("period", Map.of("from", from, "to", to));
        analytics.put("metrics", Map.of(
                "total_webhooks_received", 0,
                "successful_webhooks", 0,
                "failed_webhooks", 0,
                "average_processing_time_ms", 0
        ));

        return ResponseEntity.ok(analytics);
    }
}

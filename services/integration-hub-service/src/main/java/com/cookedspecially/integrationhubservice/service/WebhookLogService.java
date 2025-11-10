package com.cookedspecially.integrationhubservice.service;

import com.cookedspecially.integrationhubservice.domain.WebhookLog;
import com.cookedspecially.integrationhubservice.domain.WebhookStatus;
import com.cookedspecially.integrationhubservice.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookLogService {

    private final WebhookLogRepository repository;

    @Transactional
    public WebhookLog createLog(String partnerId, String webhookType, String externalOrderId,
                                String requestPayload, Map<String, String> requestHeaders) {
        log.debug("Creating webhook log: partnerId={}, type={}, externalOrderId={}",
                partnerId, webhookType, externalOrderId);

        WebhookLog log = WebhookLog.builder()
                .partnerId(partnerId)
                .webhookType(webhookType)
                .externalOrderId(externalOrderId)
                .requestPayload(requestPayload)
                .requestHeaders(requestHeaders != null ? requestHeaders.toString() : null)
                .status(WebhookStatus.RECEIVED)
                .retryCount(0)
                .build();

        return repository.save(log);
    }

    @Transactional
    public void updateLogSuccess(Long logId, String responsePayload, int responseStatus, long processingTimeMs) {
        log.debug("Updating webhook log with success: logId={}", logId);

        WebhookLog webhookLog = repository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Webhook log not found: " + logId));

        webhookLog.setResponsePayload(responsePayload);
        webhookLog.setResponseStatus(responseStatus);
        webhookLog.setStatus(WebhookStatus.COMPLETED);
        webhookLog.setProcessingTimeMs(processingTimeMs);

        repository.save(webhookLog);
    }

    @Transactional
    public void updateLogFailure(Long logId, String errorMessage, boolean scheduleRetry) {
        log.debug("Updating webhook log with failure: logId={}, scheduleRetry={}", logId, scheduleRetry);

        WebhookLog webhookLog = repository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Webhook log not found: " + logId));

        webhookLog.setErrorMessage(errorMessage);
        webhookLog.setRetryCount(webhookLog.getRetryCount() + 1);

        if (scheduleRetry) {
            webhookLog.setStatus(WebhookStatus.RETRY_SCHEDULED);
            webhookLog.setNextRetryAt(calculateNextRetryTime(webhookLog.getRetryCount()));
        } else {
            webhookLog.setStatus(WebhookStatus.DEAD_LETTER);
        }

        repository.save(webhookLog);
    }

    @Transactional(readOnly = true)
    public Page<WebhookLog> getLogsByPartner(String partnerId, Pageable pageable) {
        return repository.findByPartnerId(partnerId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<WebhookLog> getLogsByDateRange(String partnerId, LocalDateTime startDate,
                                                LocalDateTime endDate, Pageable pageable) {
        return repository.findByPartnerIdAndCreatedAtBetween(partnerId, startDate, endDate, pageable);
    }

    private LocalDateTime calculateNextRetryTime(int retryCount) {
        // Exponential backoff: 5s, 10s, 20s, etc.
        long delaySeconds = (long) (5 * Math.pow(2, retryCount));
        return LocalDateTime.now().plusSeconds(delaySeconds);
    }
}

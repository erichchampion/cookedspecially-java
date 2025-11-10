package com.cookedspecially.integrationhubservice.repository;

import com.cookedspecially.integrationhubservice.domain.WebhookLog;
import com.cookedspecially.integrationhubservice.domain.WebhookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {

    Page<WebhookLog> findByPartnerId(String partnerId, Pageable pageable);

    Page<WebhookLog> findByStatus(WebhookStatus status, Pageable pageable);

    Page<WebhookLog> findByPartnerIdAndCreatedAtBetween(String partnerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Optional<WebhookLog> findByExternalOrderId(String externalOrderId);

    List<WebhookLog> findByStatusAndNextRetryAtBefore(WebhookStatus status, LocalDateTime now);
}

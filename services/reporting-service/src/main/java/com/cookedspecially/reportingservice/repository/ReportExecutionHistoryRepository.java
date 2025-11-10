package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.domain.ExecutionStatus;
import com.cookedspecially.reportingservice.domain.ReportExecutionHistory;
import com.cookedspecially.reportingservice.domain.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ReportExecutionHistory entity.
 */
@Repository
public interface ReportExecutionHistoryRepository extends JpaRepository<ReportExecutionHistory, Long> {

    Page<ReportExecutionHistory> findByReportType(ReportType reportType, Pageable pageable);

    Page<ReportExecutionHistory> findByStatus(ExecutionStatus status, Pageable pageable);

    List<ReportExecutionHistory> findByScheduledReportId(Long scheduledReportId);

    List<ReportExecutionHistory> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<ReportExecutionHistory> findByGeneratedBy(String generatedBy);
}

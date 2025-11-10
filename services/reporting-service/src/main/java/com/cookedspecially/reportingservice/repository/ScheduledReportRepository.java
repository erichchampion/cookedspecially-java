package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.domain.ScheduledReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ScheduledReport entity.
 */
@Repository
public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, Long> {

    List<ScheduledReport> findByActiveTrue();

    List<ScheduledReport> findByRestaurantIdAndActiveTrue(Long restaurantId);
}

package com.cookedspecially.reportingservice.repository.impl;

import com.cookedspecially.reportingservice.dto.operations.DeliveryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.operations.TillSummaryDTO;
import com.cookedspecially.reportingservice.repository.OperationsReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of OperationsReportRepository with actual database queries.
 * Queries are designed to work with the Kitchen Service database schema.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class OperationsReportRepositoryImpl implements OperationsReportRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<DeliveryPerformanceDTO> generateDeliveryPerformanceReport(LocalDate fromDate, LocalDate toDate,
                                                                          Long restaurantId) {
        log.info("Generating delivery performance report from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                db.id as deliveryPersonId,
                db.name as deliveryPersonName,
                COUNT(d.id) as totalDeliveries,
                SUM(CASE WHEN d.delivered_at <= d.expected_delivery_time THEN 1 ELSE 0 END) as onTimeDeliveries,
                SUM(CASE WHEN d.delivered_at > d.expected_delivery_time THEN 1 ELSE 0 END) as lateDeliveries,
                (SUM(CASE WHEN d.delivered_at <= d.expected_delivery_time THEN 1 ELSE 0 END) * 100.0 / COUNT(d.id)) as onTimePercentage,
                AVG(TIMESTAMPDIFF(MINUTE, d.picked_up_at, d.delivered_at)) as averageDeliveryTimeMinutes,
                SUM(d.delivery_charge) as totalDeliveryChargesCollected,
                COUNT(dr.id) as customerRatingCount,
                AVG(dr.rating) as averageRating,
                CASE
                    WHEN (SUM(CASE WHEN d.delivered_at <= d.expected_delivery_time THEN 1 ELSE 0 END) * 100.0 / COUNT(d.id)) >= 90 THEN 'EXCELLENT'
                    WHEN (SUM(CASE WHEN d.delivered_at <= d.expected_delivery_time THEN 1 ELSE 0 END) * 100.0 / COUNT(d.id)) >= 75 THEN 'GOOD'
                    WHEN (SUM(CASE WHEN d.delivered_at <= d.expected_delivery_time THEN 1 ELSE 0 END) * 100.0 / COUNT(d.id)) >= 60 THEN 'AVERAGE'
                    ELSE 'POOR'
                END as performanceStatus
            FROM delivery_boys db
            LEFT JOIN deliveries d ON db.id = d.delivery_boy_id AND DATE(d.delivered_at) BETWEEN :fromDate AND :toDate
            LEFT JOIN delivery_ratings dr ON d.id = dr.delivery_id
            """);

        if (restaurantId != null) {
            sql.append(" WHERE db.restaurant_id = :restaurantId");
        }

        sql.append("""
            GROUP BY db.id, db.name
            HAVING COUNT(d.id) > 0
            ORDER BY totalDeliveries DESC
            """);

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> DeliveryPerformanceDTO.builder()
                .deliveryPersonId(((Number) row[0]).longValue())
                .deliveryPersonName((String) row[1])
                .totalDeliveries(((Number) row[2]).intValue())
                .onTimeDeliveries(((Number) row[3]).intValue())
                .lateDeliveries(((Number) row[4]).intValue())
                .onTimePercentage(((Number) row[5]).doubleValue())
                .averageDeliveryTimeMinutes(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0)
                .totalDeliveryChargesCollected((BigDecimal) row[7])
                .customerRatingCount(((Number) row[8]).intValue())
                .averageRating(row[9] != null ? ((Number) row[9]).doubleValue() : 0.0)
                .performanceStatus((String) row[10])
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<TillSummaryDTO> generateTillSummaryReport(Long tillId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating till summary report for till {} from {} to {}", tillId, fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                t.id as tillId,
                t.name as tillName,
                fc.name as fulfillmentCenterName,
                ts.opened_at as openedAt,
                ts.closed_at as closedAt,
                u.username as operatorName,
                ts.opening_balance as openingBalance,
                SUM(CASE WHEN p.payment_method = 'CASH' THEN p.amount ELSE 0 END) as totalCashSales,
                SUM(CASE WHEN p.payment_method = 'CARD' THEN p.amount ELSE 0 END) as totalCardSales,
                SUM(CASE WHEN p.payment_method IN ('UPI', 'WALLET', 'NET_BANKING') THEN p.amount ELSE 0 END) as totalOnlineSales,
                SUM(CASE WHEN tr.transaction_type = 'CASH_IN' THEN tr.amount ELSE 0 END) as cashAdded,
                SUM(CASE WHEN tr.transaction_type = 'CASH_OUT' THEN tr.amount ELSE 0 END) as cashWithdrawn,
                ts.expected_closing_balance as expectedClosingBalance,
                ts.actual_closing_balance as actualClosingBalance,
                (ts.actual_closing_balance - ts.expected_closing_balance) as variance,
                COUNT(DISTINCT p.id) as totalTransactions,
                ts.status as status
            FROM tills t
            INNER JOIN till_sessions ts ON t.id = ts.till_id
            LEFT JOIN fulfillment_centers fc ON t.fulfillment_center_id = fc.id
            LEFT JOIN users u ON ts.operator_id = u.id
            LEFT JOIN payments p ON ts.id = p.till_session_id
            LEFT JOIN till_transactions tr ON ts.id = tr.till_session_id
            WHERE t.id = :tillId
            AND DATE(ts.opened_at) BETWEEN :fromDate AND :toDate
            GROUP BY t.id, t.name, fc.name, ts.opened_at, ts.closed_at, u.username,
                     ts.opening_balance, ts.expected_closing_balance, ts.actual_closing_balance, ts.status
            ORDER BY ts.opened_at DESC
            """);

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("tillId", tillId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> TillSummaryDTO.builder()
                .tillId(((Number) row[0]).longValue())
                .tillName((String) row[1])
                .fulfillmentCenterName((String) row[2])
                .openedAt(((java.sql.Timestamp) row[3]).toLocalDateTime())
                .closedAt(row[4] != null ? ((java.sql.Timestamp) row[4]).toLocalDateTime() : null)
                .operatorName((String) row[5])
                .openingBalance((BigDecimal) row[6])
                .totalCashSales((BigDecimal) row[7])
                .totalCardSales((BigDecimal) row[8])
                .totalOnlineSales((BigDecimal) row[9])
                .cashAdded((BigDecimal) row[10])
                .cashWithdrawn((BigDecimal) row[11])
                .expectedClosingBalance((BigDecimal) row[12])
                .actualClosingBalance((BigDecimal) row[13])
                .variance((BigDecimal) row[14])
                .totalTransactions(((Number) row[15]).intValue())
                .status((String) row[16])
                .build())
            .collect(Collectors.toList());
    }
}

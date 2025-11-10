package com.cookedspecially.reportingservice.repository.impl;

import com.cookedspecially.reportingservice.dto.product.CategoryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.product.TopDishReportDTO;
import com.cookedspecially.reportingservice.repository.ProductReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductReportRepository with actual database queries.
 * Queries are designed to work with the Restaurant Service database schema.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductReportRepositoryImpl implements ProductReportRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<TopDishReportDTO> generateTopDishesReport(LocalDate fromDate, LocalDate toDate,
                                                          Long restaurantId, Integer limit) {
        log.info("Generating top dishes report from {} to {}, limit: {}", fromDate, toDate, limit);

        StringBuilder sql = new StringBuilder("""
            SELECT
                mi.id as dishId,
                mi.name as dishName,
                cat.name as category,
                SUM(oi.quantity) as totalQuantitySold,
                SUM(oi.total_price) as totalRevenue,
                AVG(oi.unit_price) as averagePrice,
                COUNT(DISTINCT oi.order_id) as orderCount,
                (SUM(oi.total_price) * 100.0 / (
                    SELECT SUM(total_price)
                    FROM order_items oi2
                    INNER JOIN orders o2 ON oi2.order_id = o2.id
                    WHERE DATE(o2.created_at) BETWEEN :fromDate AND :toDate
                )) as contributionToRevenue,
                CASE
                    WHEN SUM(oi.quantity) > LAG(SUM(oi.quantity)) OVER (ORDER BY mi.id) THEN 'UP'
                    WHEN SUM(oi.quantity) < LAG(SUM(oi.quantity)) OVER (ORDER BY mi.id) THEN 'DOWN'
                    ELSE 'STABLE'
                END as trend
            FROM order_items oi
            INNER JOIN orders o ON oi.order_id = o.id
            INNER JOIN menu_items mi ON oi.menu_item_id = mi.id
            INNER JOIN categories cat ON mi.category_id = cat.id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            """);

        if (restaurantId != null) {
            sql.append(" AND mi.restaurant_id = :restaurantId");
        }

        sql.append("""
            GROUP BY mi.id, mi.name, cat.name
            ORDER BY totalRevenue DESC
            LIMIT :limit
            """);

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("limit", limit);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> TopDishReportDTO.builder()
                .dishId(((Number) row[0]).longValue())
                .dishName((String) row[1])
                .category((String) row[2])
                .totalQuantitySold(((Number) row[3]).intValue())
                .totalRevenue((BigDecimal) row[4])
                .averagePrice((BigDecimal) row[5])
                .orderCount(((Number) row[6]).intValue())
                .contributionToRevenue((BigDecimal) row[7])
                .trend(row[8] != null ? (String) row[8] : "STABLE")
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryPerformanceDTO> generateCategoryPerformanceReport(LocalDate fromDate, LocalDate toDate,
                                                                          Long restaurantId) {
        log.info("Generating category performance report from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                cat.name as category,
                SUM(oi.quantity) as totalItemsSold,
                SUM(oi.total_price) as totalRevenue,
                (SUM(oi.total_price) * 100.0 / (
                    SELECT SUM(total_price)
                    FROM order_items oi2
                    INNER JOIN orders o2 ON oi2.order_id = o2.id
                    WHERE DATE(o2.created_at) BETWEEN :fromDate AND :toDate
                )) as percentageOfTotalRevenue,
                COUNT(DISTINCT oi.menu_item_id) as uniqueDishesOrdered,
                AVG(oi.unit_price) as averageDishPrice,
                CASE
                    WHEN SUM(oi.total_price) > (
                        SELECT SUM(oi3.total_price)
                        FROM order_items oi3
                        INNER JOIN orders o3 ON oi3.order_id = o3.id
                        INNER JOIN menu_items mi3 ON oi3.menu_item_id = mi3.id
                        WHERE mi3.category_id = cat.id
                        AND DATE(o3.created_at) BETWEEN DATE_SUB(:fromDate, INTERVAL 30 DAY) AND DATE_SUB(:toDate, INTERVAL 30 DAY)
                    ) THEN 'UP'
                    ELSE 'DOWN'
                END as trend
            FROM order_items oi
            INNER JOIN orders o ON oi.order_id = o.id
            INNER JOIN menu_items mi ON oi.menu_item_id = mi.id
            INNER JOIN categories cat ON mi.category_id = cat.id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            """);

        if (restaurantId != null) {
            sql.append(" AND mi.restaurant_id = :restaurantId");
        }

        sql.append("""
            GROUP BY cat.id, cat.name
            ORDER BY totalRevenue DESC
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
            .map(row -> CategoryPerformanceDTO.builder()
                .category((String) row[0])
                .totalItemsSold(((Number) row[1]).intValue())
                .totalRevenue((BigDecimal) row[2])
                .percentageOfTotalRevenue((BigDecimal) row[3])
                .uniqueDishesOrdered(((Number) row[4]).intValue())
                .averageDishPrice((BigDecimal) row[5])
                .trend((String) row[6])
                .build())
            .collect(Collectors.toList());
    }
}

package com.cookedspecially.reportingservice.repository.impl;

import com.cookedspecially.reportingservice.dto.customer.CustomerReportDTO;
import com.cookedspecially.reportingservice.dto.customer.CustomerSummaryDTO;
import com.cookedspecially.reportingservice.repository.CustomerReportRepository;
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
 * Implementation of CustomerReportRepository with actual database queries.
 * Queries are designed to work with the Customer Service database schema.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomerReportRepositoryImpl implements CustomerReportRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<CustomerReportDTO> generateCustomerListReport(LocalDate fromDate, LocalDate toDate, Long restaurantId) {
        log.info("Generating customer list report from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                c.id as customerId,
                CONCAT(c.first_name, ' ', c.last_name) as name,
                c.email as email,
                c.phone as phone,
                c.created_at as registrationDate,
                COUNT(o.id) as totalOrders,
                COALESCE(SUM(o.total_amount), 0) as totalSpent,
                COALESCE(AVG(o.total_amount), 0) as averageOrderValue,
                MAX(o.created_at) as lastOrderDate,
                (
                    SELECT cat.name
                    FROM order_items oi
                    INNER JOIN orders o2 ON oi.order_id = o2.id
                    INNER JOIN menu_items mi ON oi.menu_item_id = mi.id
                    INNER JOIN categories cat ON mi.category_id = cat.id
                    WHERE o2.customer_id = c.id
                    GROUP BY cat.id
                    ORDER BY COUNT(*) DESC
                    LIMIT 1
                ) as favoriteCategory,
                (
                    SELECT p.payment_method
                    FROM payments p
                    INNER JOIN orders o3 ON p.order_id = o3.id
                    WHERE o3.customer_id = c.id
                    GROUP BY p.payment_method
                    ORDER BY COUNT(*) DESC
                    LIMIT 1
                ) as preferredPaymentMethod,
                COALESCE(c.loyalty_points, 0) as loyaltyPoints,
                c.status as customerStatus
            FROM customers c
            LEFT JOIN orders o ON c.id = o.customer_id AND DATE(o.created_at) BETWEEN :fromDate AND :toDate
            WHERE c.created_at <= :toDate
            """);

        if (restaurantId != null) {
            sql.append(" AND o.restaurant_id = :restaurantId");
        }

        sql.append(" GROUP BY c.id, c.first_name, c.last_name, c.email, c.phone, c.created_at, c.loyalty_points, c.status");
        sql.append(" ORDER BY totalSpent DESC");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> CustomerReportDTO.builder()
                .customerId(((Number) row[0]).longValue())
                .name((String) row[1])
                .email((String) row[2])
                .phone((String) row[3])
                .registrationDate(((java.sql.Timestamp) row[4]).toLocalDateTime())
                .totalOrders(((Number) row[5]).intValue())
                .totalSpent((BigDecimal) row[6])
                .averageOrderValue((BigDecimal) row[7])
                .lastOrderDate(row[8] != null ? ((java.sql.Timestamp) row[8]).toLocalDateTime() : null)
                .favoriteCategory((String) row[9])
                .preferredPaymentMethod((String) row[10])
                .loyaltyPoints(((Number) row[11]).intValue())
                .customerStatus((String) row[12])
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public CustomerSummaryDTO generateCustomerSummary(LocalDate fromDate, LocalDate toDate, Long restaurantId) {
        log.info("Generating customer summary from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                COUNT(DISTINCT c.id) as totalCustomers,
                COUNT(DISTINCT CASE WHEN c.created_at BETWEEN :fromDate AND :toDate THEN c.id END) as newCustomersThisPeriod,
                COUNT(DISTINCT CASE WHEN o.created_at BETWEEN :fromDate AND :toDate THEN c.id END) as activeCustomers,
                COUNT(DISTINCT CASE WHEN c.id NOT IN (
                    SELECT customer_id FROM orders WHERE created_at BETWEEN :fromDate AND :toDate
                ) THEN c.id END) as inactiveCustomers,
                COALESCE(SUM(CASE WHEN o.created_at BETWEEN :fromDate AND :toDate THEN o.total_amount ELSE 0 END), 0) as totalRevenue,
                COALESCE(AVG(customer_totals.lifetime_value), 0) as averageCustomerLifetimeValue,
                COALESCE(AVG(o.total_amount), 0) as averageOrderValue,
                COALESCE(AVG(customer_totals.order_count), 0) as averageOrderFrequency,
                (SELECT COUNT(*) FROM (
                    SELECT customer_id, SUM(total_amount) as revenue
                    FROM orders
                    WHERE created_at BETWEEN :fromDate AND :toDate
                    GROUP BY customer_id
                    ORDER BY revenue DESC
                    LIMIT 10
                ) top_customers) as topCustomerCount
            FROM customers c
            LEFT JOIN orders o ON c.id = o.customer_id
            LEFT JOIN (
                SELECT customer_id, SUM(total_amount) as lifetime_value, COUNT(*) as order_count
                FROM orders
                GROUP BY customer_id
            ) customer_totals ON c.id = customer_totals.customer_id
            """);

        if (restaurantId != null) {
            sql.append(" WHERE o.restaurant_id = :restaurantId");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return CustomerSummaryDTO.builder()
                .totalCustomers(0)
                .newCustomersThisPeriod(0)
                .activeCustomers(0)
                .inactiveCustomers(0)
                .totalRevenue(BigDecimal.ZERO)
                .averageCustomerLifetimeValue(BigDecimal.ZERO)
                .averageOrderValue(BigDecimal.ZERO)
                .averageOrderFrequency(0.0)
                .topCustomerCount(0)
                .build();
        }

        Object[] row = results.get(0);
        return CustomerSummaryDTO.builder()
            .totalCustomers(((Number) row[0]).intValue())
            .newCustomersThisPeriod(((Number) row[1]).intValue())
            .activeCustomers(((Number) row[2]).intValue())
            .inactiveCustomers(((Number) row[3]).intValue())
            .totalRevenue((BigDecimal) row[4])
            .averageCustomerLifetimeValue((BigDecimal) row[5])
            .averageOrderValue((BigDecimal) row[6])
            .averageOrderFrequency(((Number) row[7]).doubleValue())
            .topCustomerCount(((Number) row[8]).intValue())
            .build();
    }

    @Override
    public List<CustomerReportDTO> getTopCustomersByRevenue(LocalDate fromDate, LocalDate toDate, Integer limit) {
        log.info("Getting top {} customers by revenue from {} to {}", limit, fromDate, toDate);

        String sql = """
            SELECT
                c.id as customerId,
                CONCAT(c.first_name, ' ', c.last_name) as name,
                c.email as email,
                c.phone as phone,
                c.created_at as registrationDate,
                COUNT(o.id) as totalOrders,
                SUM(o.total_amount) as totalSpent,
                AVG(o.total_amount) as averageOrderValue,
                MAX(o.created_at) as lastOrderDate,
                '' as favoriteCategory,
                '' as preferredPaymentMethod,
                COALESCE(c.loyalty_points, 0) as loyaltyPoints,
                c.status as customerStatus
            FROM customers c
            INNER JOIN orders o ON c.id = o.customer_id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            GROUP BY c.id, c.first_name, c.last_name, c.email, c.phone, c.created_at, c.loyalty_points, c.status
            ORDER BY totalSpent DESC
            LIMIT :limit
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> CustomerReportDTO.builder()
                .customerId(((Number) row[0]).longValue())
                .name((String) row[1])
                .email((String) row[2])
                .phone((String) row[3])
                .registrationDate(((java.sql.Timestamp) row[4]).toLocalDateTime())
                .totalOrders(((Number) row[5]).intValue())
                .totalSpent((BigDecimal) row[6])
                .averageOrderValue((BigDecimal) row[7])
                .lastOrderDate(((java.sql.Timestamp) row[8]).toLocalDateTime())
                .favoriteCategory((String) row[9])
                .preferredPaymentMethod((String) row[10])
                .loyaltyPoints(((Number) row[11]).intValue())
                .customerStatus((String) row[12])
                .build())
            .collect(Collectors.toList());
    }
}

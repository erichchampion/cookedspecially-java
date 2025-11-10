package com.cookedspecially.reportingservice.repository.impl;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.repository.SalesReportRepository;
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
 * Implementation of SalesReportRepository with actual database queries.
 * Queries are designed to work with the Order Service database schema.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SalesReportRepositoryImpl implements SalesReportRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<InvoiceReportDTO> generateDailyInvoiceReport(LocalDate fromDate, LocalDate toDate,
                                                             Long restaurantId, Long fulfillmentCenterId) {
        log.info("Generating daily invoice report from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                o.id as invoiceId,
                o.order_number as invoiceNumber,
                o.created_at as invoiceDate,
                CONCAT(c.first_name, ' ', c.last_name) as customerName,
                c.phone as customerPhone,
                c.email as customerEmail,
                o.order_type as orderType,
                p.payment_method as paymentMethod,
                o.subtotal as subtotal,
                o.cgst as cgst,
                o.sgst as sgst,
                o.service_tax as serviceTax,
                o.delivery_charge as deliveryCharge,
                o.discount_amount as discount,
                o.total_amount as grandTotal,
                fc.name as fulfillmentCenterName,
                r.name as restaurantName,
                o.status as status,
                o.created_at as createdAt
            FROM orders o
            LEFT JOIN customers c ON o.customer_id = c.id
            LEFT JOIN payments p ON o.id = p.order_id
            LEFT JOIN fulfillment_centers fc ON o.fulfillment_center_id = fc.id
            LEFT JOIN restaurants r ON o.restaurant_id = r.id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            """);

        if (restaurantId != null) {
            sql.append(" AND o.restaurant_id = :restaurantId");
        }
        if (fulfillmentCenterId != null) {
            sql.append(" AND o.fulfillment_center_id = :fulfillmentCenterId");
        }

        sql.append(" ORDER BY o.created_at DESC");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }
        if (fulfillmentCenterId != null) {
            query.setParameter("fulfillmentCenterId", fulfillmentCenterId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> InvoiceReportDTO.builder()
                .invoiceId(((Number) row[0]).longValue())
                .invoiceNumber((String) row[1])
                .invoiceDate(((java.sql.Timestamp) row[2]).toLocalDateTime())
                .customerName((String) row[3])
                .customerPhone((String) row[4])
                .customerEmail((String) row[5])
                .orderType((String) row[6])
                .paymentMethod((String) row[7])
                .subtotal((BigDecimal) row[8])
                .cgst((BigDecimal) row[9])
                .sgst((BigDecimal) row[10])
                .serviceTax((BigDecimal) row[11])
                .deliveryCharge((BigDecimal) row[12])
                .discount((BigDecimal) row[13])
                .grandTotal((BigDecimal) row[14])
                .fulfillmentCenterName((String) row[15])
                .restaurantName((String) row[16])
                .status((String) row[17])
                .createdAt(((java.sql.Timestamp) row[18]).toLocalDateTime())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<DailySalesSummaryDTO> generateDailySalesSummary(LocalDate fromDate, LocalDate toDate,
                                                                Long fulfillmentCenterId) {
        log.info("Generating daily sales summary from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                DATE(o.created_at) as date,
                fc.name as fulfillmentCenterName,
                COUNT(o.id) as totalOrders,
                SUM(CASE WHEN o.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedOrders,
                SUM(CASE WHEN o.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledOrders,
                SUM(o.total_amount) as totalSales,
                SUM(CASE WHEN p.payment_method = 'CASH' THEN o.total_amount ELSE 0 END) as cashSales,
                SUM(CASE WHEN p.payment_method = 'CARD' THEN o.total_amount ELSE 0 END) as cardSales,
                SUM(CASE WHEN p.payment_method IN ('UPI', 'WALLET', 'NET_BANKING') THEN o.total_amount ELSE 0 END) as onlineSales,
                SUM(o.cgst + o.sgst + o.service_tax) as totalTax,
                SUM(o.discount_amount) as totalDiscount,
                SUM(o.total_amount - (o.cgst + o.sgst + o.service_tax)) as netSales,
                AVG(o.total_amount) as averageOrderValue,
                COUNT(DISTINCT CASE WHEN c.created_at >= :fromDate THEN c.id END) as newCustomers,
                COUNT(DISTINCT CASE WHEN c.created_at < :fromDate THEN c.id END) as returningCustomers
            FROM orders o
            LEFT JOIN payments p ON o.id = p.order_id
            LEFT JOIN fulfillment_centers fc ON o.fulfillment_center_id = fc.id
            LEFT JOIN customers c ON o.customer_id = c.id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            """);

        if (fulfillmentCenterId != null) {
            sql.append(" AND o.fulfillment_center_id = :fulfillmentCenterId");
        }

        sql.append(" GROUP BY DATE(o.created_at), fc.name ORDER BY date DESC");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (fulfillmentCenterId != null) {
            query.setParameter("fulfillmentCenterId", fulfillmentCenterId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> DailySalesSummaryDTO.builder()
                .date(((java.sql.Date) row[0]).toLocalDate())
                .fulfillmentCenterName((String) row[1])
                .totalOrders(((Number) row[2]).intValue())
                .completedOrders(((Number) row[3]).intValue())
                .cancelledOrders(((Number) row[4]).intValue())
                .totalSales((BigDecimal) row[5])
                .cashSales((BigDecimal) row[6])
                .cardSales((BigDecimal) row[7])
                .onlineSales((BigDecimal) row[8])
                .totalTax((BigDecimal) row[9])
                .totalDiscount((BigDecimal) row[10])
                .netSales((BigDecimal) row[11])
                .averageOrderValue((BigDecimal) row[12])
                .newCustomers(((Number) row[13]).intValue())
                .returningCustomers(((Number) row[14]).intValue())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<DetailedInvoiceReportDTO> generateDetailedInvoiceReport(LocalDate fromDate, LocalDate toDate,
                                                                        Long restaurantId) {
        log.info("Generating detailed invoice report from {} to {}", fromDate, toDate);

        StringBuilder sql = new StringBuilder("""
            SELECT
                o.order_number as invoiceNumber,
                o.created_at as invoiceDate,
                CONCAT(c.first_name, ' ', c.last_name) as customerName,
                mi.name as dishName,
                cat.name as category,
                oi.quantity as quantity,
                oi.unit_price as unitPrice,
                oi.total_price as lineTotal,
                oi.add_ons as addOns,
                oi.discount_amount as discount,
                oi.tax_amount as tax,
                p.payment_method as paymentMethod,
                fc.name as fulfillmentCenterName
            FROM order_items oi
            INNER JOIN orders o ON oi.order_id = o.id
            LEFT JOIN customers c ON o.customer_id = c.id
            LEFT JOIN menu_items mi ON oi.menu_item_id = mi.id
            LEFT JOIN categories cat ON mi.category_id = cat.id
            LEFT JOIN payments p ON o.id = p.order_id
            LEFT JOIN fulfillment_centers fc ON o.fulfillment_center_id = fc.id
            WHERE DATE(o.created_at) BETWEEN :fromDate AND :toDate
            """);

        if (restaurantId != null) {
            sql.append(" AND o.restaurant_id = :restaurantId");
        }

        sql.append(" ORDER BY o.created_at DESC, oi.id");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (restaurantId != null) {
            query.setParameter("restaurantId", restaurantId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(row -> DetailedInvoiceReportDTO.builder()
                .invoiceNumber((String) row[0])
                .invoiceDate(((java.sql.Timestamp) row[1]).toLocalDateTime())
                .customerName((String) row[2])
                .dishName((String) row[3])
                .category((String) row[4])
                .quantity(((Number) row[5]).intValue())
                .unitPrice((BigDecimal) row[6])
                .lineTotal((BigDecimal) row[7])
                .addOns((String) row[8])
                .discount((BigDecimal) row[9])
                .tax((BigDecimal) row[10])
                .paymentMethod((String) row[11])
                .fulfillmentCenterName((String) row[12])
                .build())
            .collect(Collectors.toList());
    }
}

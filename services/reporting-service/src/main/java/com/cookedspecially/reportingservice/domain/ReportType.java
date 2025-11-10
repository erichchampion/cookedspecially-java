package com.cookedspecially.reportingservice.domain;

/**
 * Enum representing different types of reports.
 */
public enum ReportType {
    // Sales Reports
    DAILY_INVOICE,
    DAILY_SALES_SUMMARY,
    DETAILED_INVOICE,
    SALES_REGISTER,
    SALES_SUMMARY,

    // Customer Reports
    CUSTOMER_LIST,
    CUSTOMER_SUMMARY,
    CUSTOMER_PURCHASE_PATTERNS,
    CUSTOMER_ORDER_HISTORY,

    // Product Reports
    TOP_DISHES,
    CATEGORY_PERFORMANCE,
    MENU_ITEM_ANALYTICS,
    SLOW_MOVING_ITEMS,

    // Operations Reports
    DELIVERY_PERFORMANCE,
    TILL_SUMMARY,
    TRANSACTION_REPORT,
    PAYMENT_RECONCILIATION
}

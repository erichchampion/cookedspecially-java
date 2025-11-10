// Reporting Service API Client

import ApiClient from './api-client.js';

class ReportingApi extends ApiClient {
    constructor() {
        super('reporting');
    }

    // Sales Reports
    async getSalesReport(restaurantId, startDate, endDate) {
        return this.get(`/reports/sales/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getSalesByDay(restaurantId, startDate, endDate) {
        return this.get(`/reports/sales/daily/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getSalesByHour(restaurantId, date) {
        return this.get(`/reports/sales/hourly/restaurant/${restaurantId}?date=${date}`);
    }

    async getTopSellingItems(restaurantId, startDate, endDate, limit = 10) {
        return this.get(`/reports/sales/top-items/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}&limit=${limit}`);
    }

    // Customer Reports
    async getCustomerReport(customerId, startDate, endDate) {
        return this.get(`/reports/customers/${customerId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getCustomerLifetimeValue(customerId) {
        return this.get(`/reports/customers/${customerId}/lifetime-value`);
    }

    async getNewCustomers(restaurantId, startDate, endDate) {
        return this.get(`/reports/customers/new/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getCustomerRetention(restaurantId, startDate, endDate) {
        return this.get(`/reports/customers/retention/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    // Order Reports
    async getOrderMetrics(restaurantId, startDate, endDate) {
        return this.get(`/reports/orders/metrics/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getOrdersByStatus(restaurantId, startDate, endDate) {
        return this.get(`/reports/orders/by-status/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getAverageOrderValue(restaurantId, startDate, endDate) {
        return this.get(`/reports/orders/average-value/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    // Performance Reports
    async getRestaurantPerformance(restaurantId, startDate, endDate) {
        return this.get(`/reports/performance/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getDeliveryPerformance(restaurantId, startDate, endDate) {
        return this.get(`/reports/performance/delivery/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getKitchenPerformance(restaurantId, startDate, endDate) {
        return this.get(`/reports/performance/kitchen/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    // Financial Reports
    async getRevenueSummary(restaurantId, startDate, endDate) {
        return this.get(`/reports/financial/revenue/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getPaymentMethodsBreakdown(restaurantId, startDate, endDate) {
        return this.get(`/reports/financial/payment-methods/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    async getRefundsReport(restaurantId, startDate, endDate) {
        return this.get(`/reports/financial/refunds/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
    }

    // Export Reports
    async exportReport(reportType, format, params) {
        return this.post('/reports/export', {
            report_type: reportType,
            format,
            params
        });
    }

    // Dashboard Summary
    async getDashboardSummary(restaurantId) {
        return this.get(`/reports/dashboard/restaurant/${restaurantId}`);
    }
}

export default new ReportingApi();

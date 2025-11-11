// Reporting Service Page Controller

import reportingApi from '../modules/reporting-api.js';
import { displayResult, displayError, displayTable, checkHealth, setDefaultDates, formatDate, formatCurrency, formatNumber } from '../utils.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Set default date range (last 30 days)
    setDefaultDates();

    // Check service health on load
    checkHealth(reportingApi, 'service-status');

    // Health check button
    const healthBtn = document.getElementById('check-health');
    if (healthBtn) {
        healthBtn.addEventListener('click', () => {
            checkHealth(reportingApi, 'service-status');
        });
    }

    // Helper function to get report parameters
    function getReportParams() {
        const restaurantId = document.getElementById('restaurant-id').value;
        const startDate = document.getElementById('start-date').value;
        const endDate = document.getElementById('end-date').value;

        if (!restaurantId) {
            return { error: 'Please enter a restaurant ID' };
        }
        if (!startDate || !endDate) {
            return { error: 'Please select start and end dates' };
        }

        return { restaurantId, startDate, endDate };
    }

    // Dashboard summary
    const getDashboardBtn = document.getElementById('get-dashboard-btn');
    if (getDashboardBtn) {
        getDashboardBtn.addEventListener('click', async () => {
            const restaurantId = document.getElementById('restaurant-id').value;
            if (!restaurantId) {
                displayError('dashboard-result', 'Please enter a restaurant ID');
                return;
            }

            try {
                const result = await reportingApi.getDashboardSummary(restaurantId);
                if (result.success) {
                    displayResult('dashboard-result', result.data);
                } else {
                    displayError('dashboard-result', result.error);
                }
            } catch (error) {
                displayError('dashboard-result', error.message);
            }
        });
    }

    // Sales report
    const getSalesBtn = document.getElementById('get-sales-btn');
    if (getSalesBtn) {
        getSalesBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('sales-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getSalesReport(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('sales-result', result.data);
                } else {
                    displayError('sales-result', result.error);
                }
            } catch (error) {
                displayError('sales-result', error.message);
            }
        });
    }

    // Daily sales
    const getDailySalesBtn = document.getElementById('get-daily-sales-btn');
    if (getDailySalesBtn) {
        getDailySalesBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('daily-sales-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getSalesByDay(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayTable('daily-sales-result', result.data, [
                        { field: 'date', label: 'Date', format: formatDate },
                        { field: 'total_orders', label: 'Orders', format: formatNumber },
                        { field: 'total_revenue', label: 'Revenue', format: formatCurrency },
                        { field: 'average_order_value', label: 'Avg Order', format: formatCurrency }
                    ]);
                } else {
                    displayError('daily-sales-result', result.error);
                }
            } catch (error) {
                displayError('daily-sales-result', error.message);
            }
        });
    }

    // Top selling items
    const getTopItemsBtn = document.getElementById('get-top-items-btn');
    if (getTopItemsBtn) {
        getTopItemsBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('top-items-result', params.error);
                return;
            }

            const limit = document.getElementById('top-items-limit').value || 10;

            try {
                const result = await reportingApi.getTopSellingItems(params.restaurantId, params.startDate, params.endDate, limit);
                if (result.success) {
                    displayTable('top-items-result', result.data, [
                        { field: 'item_name', label: 'Item' },
                        { field: 'quantity_sold', label: 'Quantity', format: formatNumber },
                        { field: 'revenue', label: 'Revenue', format: formatCurrency }
                    ]);
                } else {
                    displayError('top-items-result', result.error);
                }
            } catch (error) {
                displayError('top-items-result', error.message);
            }
        });
    }

    // Order metrics
    const getOrderMetricsBtn = document.getElementById('get-order-metrics-btn');
    if (getOrderMetricsBtn) {
        getOrderMetricsBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('order-metrics-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getOrderMetrics(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('order-metrics-result', result.data);
                } else {
                    displayError('order-metrics-result', result.error);
                }
            } catch (error) {
                displayError('order-metrics-result', error.message);
            }
        });
    }

    // Average order value
    const getAovBtn = document.getElementById('get-aov-btn');
    if (getAovBtn) {
        getAovBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('aov-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getAverageOrderValue(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('aov-result', result.data);
                } else {
                    displayError('aov-result', result.error);
                }
            } catch (error) {
                displayError('aov-result', error.message);
            }
        });
    }

    // Customer retention
    const getRetentionBtn = document.getElementById('get-retention-btn');
    if (getRetentionBtn) {
        getRetentionBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('retention-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getCustomerRetention(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('retention-result', result.data);
                } else {
                    displayError('retention-result', result.error);
                }
            } catch (error) {
                displayError('retention-result', error.message);
            }
        });
    }

    // Restaurant performance
    const getPerformanceBtn = document.getElementById('get-performance-btn');
    if (getPerformanceBtn) {
        getPerformanceBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('performance-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getRestaurantPerformance(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('performance-result', result.data);
                } else {
                    displayError('performance-result', result.error);
                }
            } catch (error) {
                displayError('performance-result', error.message);
            }
        });
    }

    // Revenue summary
    const getRevenueBtn = document.getElementById('get-revenue-btn');
    if (getRevenueBtn) {
        getRevenueBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('revenue-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getRevenueSummary(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayResult('revenue-result', result.data);
                } else {
                    displayError('revenue-result', result.error);
                }
            } catch (error) {
                displayError('revenue-result', error.message);
            }
        });
    }

    // Payment methods breakdown
    const getPaymentMethodsBtn = document.getElementById('get-payment-methods-btn');
    if (getPaymentMethodsBtn) {
        getPaymentMethodsBtn.addEventListener('click', async () => {
            const params = getReportParams();
            if (params.error) {
                displayError('payment-methods-result', params.error);
                return;
            }

            try {
                const result = await reportingApi.getPaymentMethodsBreakdown(params.restaurantId, params.startDate, params.endDate);
                if (result.success) {
                    displayTable('payment-methods-result', result.data, [
                        { field: 'payment_method', label: 'Method' },
                        { field: 'count', label: 'Count', format: formatNumber },
                        { field: 'total_amount', label: 'Total', format: formatCurrency },
                        { field: 'percentage', label: 'Percentage', format: (val) => `${val}%` }
                    ]);
                } else {
                    displayError('payment-methods-result', result.error);
                }
            } catch (error) {
                displayError('payment-methods-result', error.message);
            }
        });
    }
});

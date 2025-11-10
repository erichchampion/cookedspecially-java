// Order Service Page Controller

import orderApi from '../modules/order-api.js';
import { displayResult, displayError, displayTable, getFormData, clearForm, checkHealth, formatDate, formatCurrency } from '../utils.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Check service health on load
    checkHealth(orderApi, 'service-status');

    // Health check button
    const healthBtn = document.getElementById('check-health');
    if (healthBtn) {
        healthBtn.addEventListener('click', () => {
            checkHealth(orderApi, 'service-status');
        });
    }

    // Create order form
    const createForm = document.getElementById('create-order-form');
    if (createForm) {
        createForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(createForm);

            // Parse items JSON
            try {
                data.items = JSON.parse(data.items);
            } catch (error) {
                displayError('create-result', 'Invalid JSON format for items');
                return;
            }

            try {
                const result = await orderApi.createOrder(data);
                if (result.success) {
                    displayResult('create-result', result.data);
                    clearForm(createForm);
                } else {
                    displayError('create-result', result.error);
                }
            } catch (error) {
                displayError('create-result', error.message);
            }
        });
    }

    // Get order by ID
    const getBtn = document.getElementById('get-order-btn');
    if (getBtn) {
        getBtn.addEventListener('click', async () => {
            const id = document.getElementById('get-order-id').value;
            if (!id) {
                displayError('get-order-result', 'Please enter an order ID');
                return;
            }

            try {
                const result = await orderApi.getOrder(id);
                if (result.success) {
                    displayResult('get-order-result', result.data);
                } else {
                    displayError('get-order-result', result.error);
                }
            } catch (error) {
                displayError('get-order-result', error.message);
            }
        });
    }

    // List orders
    const listBtn = document.getElementById('list-orders-btn');
    if (listBtn) {
        listBtn.addEventListener('click', async () => {
            const page = document.getElementById('list-page').value || 0;
            const size = document.getElementById('list-size').value || 20;

            try {
                const result = await orderApi.listOrders(page, size);
                if (result.success) {
                    displayTable('orders-list', result.data.content || result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'customer_id', label: 'Customer' },
                        { field: 'restaurant_id', label: 'Restaurant' },
                        { field: 'status', label: 'Status' },
                        { field: 'total_amount', label: 'Total', format: formatCurrency },
                        { field: 'created_at', label: 'Created', format: formatDate }
                    ]);
                } else {
                    displayError('orders-list', result.error);
                }
            } catch (error) {
                displayError('orders-list', error.message);
            }
        });
    }

    // Get orders by customer
    const customerOrdersBtn = document.getElementById('customer-orders-btn');
    if (customerOrdersBtn) {
        customerOrdersBtn.addEventListener('click', async () => {
            const customerId = document.getElementById('customer-orders-id').value;
            if (!customerId) {
                displayError('customer-orders-result', 'Please enter a customer ID');
                return;
            }

            try {
                const result = await orderApi.getOrdersByCustomer(customerId);
                if (result.success) {
                    displayTable('customer-orders-result', result.data.content || result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'restaurant_id', label: 'Restaurant' },
                        { field: 'status', label: 'Status' },
                        { field: 'total_amount', label: 'Total', format: formatCurrency },
                        { field: 'created_at', label: 'Created', format: formatDate }
                    ]);
                } else {
                    displayError('customer-orders-result', result.error);
                }
            } catch (error) {
                displayError('customer-orders-result', error.message);
            }
        });
    }

    // Get orders by restaurant
    const restaurantOrdersBtn = document.getElementById('restaurant-orders-btn');
    if (restaurantOrdersBtn) {
        restaurantOrdersBtn.addEventListener('click', async () => {
            const restaurantId = document.getElementById('restaurant-orders-id').value;
            if (!restaurantId) {
                displayError('restaurant-orders-result', 'Please enter a restaurant ID');
                return;
            }

            try {
                const result = await orderApi.getOrdersByRestaurant(restaurantId);
                if (result.success) {
                    displayTable('restaurant-orders-result', result.data.content || result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'customer_id', label: 'Customer' },
                        { field: 'status', label: 'Status' },
                        { field: 'total_amount', label: 'Total', format: formatCurrency },
                        { field: 'created_at', label: 'Created', format: formatDate }
                    ]);
                } else {
                    displayError('restaurant-orders-result', result.error);
                }
            } catch (error) {
                displayError('restaurant-orders-result', error.message);
            }
        });
    }

    // Update order status
    const updateStatusBtn = document.getElementById('update-status-btn');
    if (updateStatusBtn) {
        updateStatusBtn.addEventListener('click', async () => {
            const orderId = document.getElementById('update-order-id').value;
            const status = document.getElementById('update-order-status').value;

            if (!orderId) {
                displayError('update-status-result', 'Please enter an order ID');
                return;
            }

            try {
                const result = await orderApi.updateOrderStatus(orderId, status);
                if (result.success) {
                    displayResult('update-status-result', result.data);
                } else {
                    displayError('update-status-result', result.error);
                }
            } catch (error) {
                displayError('update-status-result', error.message);
            }
        });
    }
});

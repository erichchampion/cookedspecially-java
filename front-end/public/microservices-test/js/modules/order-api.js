// Order Service API Client

import ApiClient from './api-client.js';

class OrderApi extends ApiClient {
    constructor() {
        super('order');
    }

    // Order Management
    async createOrder(orderData) {
        return this.post('/orders', orderData);
    }

    async getOrder(id) {
        return this.get(`/orders/${id}`);
    }

    async listOrders(page = 0, size = 20) {
        return this.get(`/orders?page=${page}&size=${size}`);
    }

    async getOrdersByCustomer(customerId, page = 0, size = 20) {
        return this.get(`/orders/customer/${customerId}?page=${page}&size=${size}`);
    }

    async getOrdersByRestaurant(restaurantId, page = 0, size = 20) {
        return this.get(`/orders/restaurant/${restaurantId}?page=${page}&size=${size}`);
    }

    async updateOrderStatus(id, status) {
        return this.patch(`/orders/${id}/status`, { status });
    }

    async cancelOrder(id, reason) {
        return this.post(`/orders/${id}/cancel`, { reason });
    }

    async getOrderHistory(customerId) {
        return this.get(`/orders/history/${customerId}`);
    }
}

export default new OrderApi();

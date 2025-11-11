// Payment Service API Client

import ApiClient from './api-client.js';

class PaymentApi extends ApiClient {
    constructor() {
        super('payment');
    }

    // Payment Processing
    async createPayment(paymentData) {
        return this.post('/payments', paymentData);
    }

    async getPayment(id) {
        return this.get(`/payments/${id}`);
    }

    async getPaymentsByOrder(orderId) {
        return this.get(`/payments/order/${orderId}`);
    }

    async getPaymentsByCustomer(customerId, page = 0, size = 20) {
        return this.get(`/payments/customer/${customerId}?page=${page}&size=${size}`);
    }

    async processRefund(paymentId, refundData) {
        return this.post(`/payments/${paymentId}/refund`, refundData);
    }

    async getRefund(id) {
        return this.get(`/refunds/${id}`);
    }

    async getRefundsByPayment(paymentId) {
        return this.get(`/refunds/payment/${paymentId}`);
    }

    // Payment Methods
    async getPaymentMethods(customerId) {
        return this.get(`/payment-methods/customer/${customerId}`);
    }

    async addPaymentMethod(customerId, methodData) {
        return this.post(`/payment-methods`, { ...methodData, customer_id: customerId });
    }

    async deletePaymentMethod(id) {
        return this.delete(`/payment-methods/${id}`);
    }

    async setDefaultPaymentMethod(customerId, methodId) {
        return this.put(`/payment-methods/${methodId}/default`, { customer_id: customerId });
    }

    // Webhooks (Stripe)
    async handleStripeWebhook(event) {
        return this.post('/webhooks/stripe', event);
    }
}

export default new PaymentApi();

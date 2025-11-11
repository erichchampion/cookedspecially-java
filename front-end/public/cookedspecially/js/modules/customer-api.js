// Customer Service API Client

import ApiClient from './api-client.js';

class CustomerApi extends ApiClient {
    constructor() {
        super('customer');
    }

    // Customer Management
    async register(customerData) {
        return this.post('/customers/register', customerData);
    }

    async login(credentials) {
        return this.post('/customers/login', credentials);
    }

    async getCustomer(id) {
        return this.get(`/customers/${id}`);
    }

    async updateCustomer(id, customerData) {
        return this.put(`/customers/${id}`, customerData);
    }

    async deleteCustomer(id) {
        return this.delete(`/customers/${id}`);
    }

    async listCustomers(page = 0, size = 20) {
        return this.get(`/customers?page=${page}&size=${size}`);
    }

    async searchCustomers(query) {
        return this.get(`/customers/search?q=${encodeURIComponent(query)}`);
    }

    // Addresses
    async getAddresses(customerId) {
        return this.get(`/customers/${customerId}/addresses`);
    }

    async addAddress(customerId, address) {
        return this.post(`/customers/${customerId}/addresses`, address);
    }

    async updateAddress(customerId, addressId, address) {
        return this.put(`/customers/${customerId}/addresses/${addressId}`, address);
    }

    async deleteAddress(customerId, addressId) {
        return this.delete(`/customers/${customerId}/addresses/${addressId}`);
    }
}

export default new CustomerApi();

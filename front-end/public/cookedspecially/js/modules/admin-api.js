// Admin Service API Client

import ApiClient from './api-client.js';

class AdminApi extends ApiClient {
    constructor() {
        super('admin');
    }

    // Authentication
    async login(credentials) {
        return this.post('/auth/login', credentials);
    }

    async logout() {
        return this.post('/auth/logout');
    }

    async generateOtp(userId, type) {
        return this.post('/auth/otp/generate', { user_id: userId, type });
    }

    async validateOtp(userId, code) {
        return this.post('/auth/otp/validate', { user_id: userId, code });
    }

    async resetPassword(userId, otpCode, newPassword) {
        return this.post('/auth/password/reset', {
            user_id: userId,
            otp_code: otpCode,
            new_password: newPassword
        });
    }

    // User Management
    async createUser(userData) {
        return this.post('/users', userData);
    }

    async getUser(id) {
        return this.get(`/users/${id}`);
    }

    async updateUser(id, userData) {
        return this.put(`/users/${id}`, userData);
    }

    async deleteUser(id) {
        return this.delete(`/users/${id}`);
    }

    async listUsers() {
        return this.get('/users');
    }

    // Employee Management
    async createEmployee(employeeData) {
        return this.post('/employees', employeeData);
    }

    async getEmployee(id) {
        return this.get(`/employees/${id}`);
    }

    async getEmployeesByRestaurant(restaurantId) {
        return this.get(`/employees?restaurantId=${restaurantId}`);
    }

    async deleteEmployee(id) {
        return this.delete(`/employees/${id}`);
    }

    // Role Management
    async createRole(roleData) {
        return this.post('/roles', roleData);
    }

    async getRole(id) {
        return this.get(`/roles/${id}`);
    }

    async listRoles() {
        return this.get('/roles');
    }

    async deleteRole(id) {
        return this.delete(`/roles/${id}`);
    }
}

export default new AdminApi();

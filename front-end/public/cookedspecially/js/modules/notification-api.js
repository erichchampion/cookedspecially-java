// Notification Service API Client

import ApiClient from './api-client.js';

class NotificationApi extends ApiClient {
    constructor() {
        super('notification');
    }

    // Send Notifications
    async sendEmail(emailData) {
        return this.post('/notifications/email', emailData);
    }

    async sendSms(smsData) {
        return this.post('/notifications/sms', smsData);
    }

    async sendPushNotification(pushData) {
        return this.post('/notifications/push', pushData);
    }

    async sendBulkNotification(notificationData) {
        return this.post('/notifications/bulk', notificationData);
    }

    // Notification History
    async getNotifications(userId, page = 0, size = 20) {
        return this.get(`/notifications/user/${userId}?page=${page}&size=${size}`);
    }

    async getNotification(id) {
        return this.get(`/notifications/${id}`);
    }

    async markAsRead(id) {
        return this.patch(`/notifications/${id}/read`);
    }

    async markAllAsRead(userId) {
        return this.patch(`/notifications/user/${userId}/read-all`);
    }

    async deleteNotification(id) {
        return this.delete(`/notifications/${id}`);
    }

    // Notification Preferences
    async getPreferences(userId) {
        return this.get(`/notifications/preferences/user/${userId}`);
    }

    async updatePreferences(userId, preferences) {
        return this.put(`/notifications/preferences/user/${userId}`, preferences);
    }

    // Templates
    async getTemplates() {
        return this.get('/notifications/templates');
    }

    async getTemplate(id) {
        return this.get(`/notifications/templates/${id}`);
    }

    async createTemplate(templateData) {
        return this.post('/notifications/templates', templateData);
    }

    async updateTemplate(id, templateData) {
        return this.put(`/notifications/templates/${id}`, templateData);
    }

    async deleteTemplate(id) {
        return this.delete(`/notifications/templates/${id}`);
    }

    // Push Token Management
    async registerPushToken(userId, token, platform) {
        return this.post('/notifications/push/tokens', {
            user_id: userId,
            token,
            platform
        });
    }

    async deletePushToken(userId, token) {
        return this.delete(`/notifications/push/tokens`, {
            body: { user_id: userId, token }
        });
    }

    // Notification Stats
    async getNotificationStats(startDate, endDate) {
        return this.get(`/notifications/stats?startDate=${startDate}&endDate=${endDate}`);
    }

    async getDeliveryStatus(id) {
        return this.get(`/notifications/${id}/status`);
    }
}

export default new NotificationApi();

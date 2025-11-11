// Integration Hub Service API Client

import ApiClient from './api-client.js';

class IntegrationApi extends ApiClient {
    constructor() {
        super('integration');
    }

    // Zomato Integration
    async getZomatoOrders(restaurantId, page = 0, size = 20) {
        return this.get(`/integrations/zomato/orders/restaurant/${restaurantId}?page=${page}&size=${size}`);
    }

    async syncZomatoMenu(restaurantId) {
        return this.post(`/integrations/zomato/menu/sync`, { restaurant_id: restaurantId });
    }

    async getZomatoConfig(restaurantId) {
        return this.get(`/integrations/zomato/config/restaurant/${restaurantId}`);
    }

    async updateZomatoConfig(restaurantId, config) {
        return this.put(`/integrations/zomato/config/restaurant/${restaurantId}`, config);
    }

    // Social Media Connectors
    async getSocialConnectors(restaurantId) {
        return this.get(`/integrations/social/restaurant/${restaurantId}`);
    }

    async createSocialConnector(connectorData) {
        return this.post('/integrations/social', connectorData);
    }

    async updateSocialConnector(id, connectorData) {
        return this.put(`/integrations/social/${id}`, connectorData);
    }

    async deleteSocialConnector(id) {
        return this.delete(`/integrations/social/${id}`);
    }

    async testSocialConnection(id) {
        return this.post(`/integrations/social/${id}/test`);
    }

    async postToSocial(id, content) {
        return this.post(`/integrations/social/${id}/post`, content);
    }

    // Generic Webhooks
    async getWebhookConfigs(restaurantId) {
        return this.get(`/integrations/webhooks/restaurant/${restaurantId}`);
    }

    async createWebhookConfig(configData) {
        return this.post('/integrations/webhooks', configData);
    }

    async updateWebhookConfig(id, configData) {
        return this.put(`/integrations/webhooks/${id}`, configData);
    }

    async deleteWebhookConfig(id) {
        return this.delete(`/integrations/webhooks/${id}`);
    }

    async testWebhook(id) {
        return this.post(`/integrations/webhooks/${id}/test`);
    }

    // Webhook Logs
    async getWebhookLogs(restaurantId, page = 0, size = 20) {
        return this.get(`/integrations/webhooks/logs/restaurant/${restaurantId}?page=${page}&size=${size}`);
    }

    async getWebhookLog(id) {
        return this.get(`/integrations/webhooks/logs/${id}`);
    }

    async retryWebhook(logId) {
        return this.post(`/integrations/webhooks/logs/${logId}/retry`);
    }

    // Integration Status
    async getIntegrationStatus(restaurantId) {
        return this.get(`/integrations/status/restaurant/${restaurantId}`);
    }

    async syncAllIntegrations(restaurantId) {
        return this.post(`/integrations/sync/restaurant/${restaurantId}`);
    }
}

export default new IntegrationApi();

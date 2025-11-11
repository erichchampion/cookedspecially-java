// Kitchen Service API Client

import ApiClient from './api-client.js';

class KitchenApi extends ApiClient {
    constructor() {
        super('kitchen');
    }

    // Kitchen Orders
    async getKitchenOrders(restaurantId, status = null) {
        let url = `/kitchen/orders/restaurant/${restaurantId}`;
        if (status) {
            url += `?status=${status}`;
        }
        return this.get(url);
    }

    async getKitchenOrder(id) {
        return this.get(`/kitchen/orders/${id}`);
    }

    async updateKitchenOrderStatus(id, status, notes = '') {
        return this.patch(`/kitchen/orders/${id}/status`, { status, notes });
    }

    async assignKitchenOrder(id, stationId) {
        return this.post(`/kitchen/orders/${id}/assign`, { station_id: stationId });
    }

    async startPreparation(id) {
        return this.post(`/kitchen/orders/${id}/start`);
    }

    async completePreparation(id) {
        return this.post(`/kitchen/orders/${id}/complete`);
    }

    // Kitchen Stations
    async getStations(restaurantId) {
        return this.get(`/kitchen/stations/restaurant/${restaurantId}`);
    }

    async createStation(stationData) {
        return this.post('/kitchen/stations', stationData);
    }

    async updateStation(id, stationData) {
        return this.put(`/kitchen/stations/${id}`, stationData);
    }

    async deleteStation(id) {
        return this.delete(`/kitchen/stations/${id}`);
    }

    async getStationOrders(stationId) {
        return this.get(`/kitchen/stations/${stationId}/orders`);
    }

    // Kitchen Display System (KDS)
    async getKdsDisplay(restaurantId) {
        return this.get(`/kitchen/kds/restaurant/${restaurantId}`);
    }

    async updateDisplaySettings(restaurantId, settings) {
        return this.put(`/kitchen/kds/restaurant/${restaurantId}/settings`, settings);
    }

    // Prep Times
    async getAveragePrepTime(restaurantId) {
        return this.get(`/kitchen/analytics/prep-time/restaurant/${restaurantId}`);
    }

    async getItemPrepTimes(restaurantId) {
        return this.get(`/kitchen/analytics/prep-time/items/restaurant/${restaurantId}`);
    }

    // WebSocket connection for real-time updates
    connectWebSocket(restaurantId, onMessage, onError) {
        const wsUrl = this.baseUrl.replace(/^http/, 'ws') + `/kitchen/ws/restaurant/${restaurantId}`;
        const ws = new WebSocket(wsUrl);

        ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            onMessage(data);
        };

        ws.onerror = (error) => {
            console.error('WebSocket error:', error);
            if (onError) onError(error);
        };

        return ws;
    }
}

export default new KitchenApi();

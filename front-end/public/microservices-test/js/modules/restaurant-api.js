// Restaurant Service API Client

import ApiClient from './api-client.js';

class RestaurantApi extends ApiClient {
    constructor() {
        super('restaurant');
    }

    // Restaurant Management
    async createRestaurant(restaurantData) {
        return this.post('/restaurants', restaurantData);
    }

    async getRestaurant(id) {
        return this.get(`/restaurants/${id}`);
    }

    async updateRestaurant(id, restaurantData) {
        return this.put(`/restaurants/${id}`, restaurantData);
    }

    async listRestaurants(page = 0, size = 20) {
        return this.get(`/restaurants?page=${page}&size=${size}`);
    }

    async deleteRestaurant(id) {
        return this.delete(`/restaurants/${id}`);
    }

    async searchRestaurants(query) {
        return this.get(`/restaurants/search?q=${encodeURIComponent(query)}`);
    }

    // Menu Management
    async getMenu(restaurantId) {
        return this.get(`/restaurants/${restaurantId}/menu`);
    }

    async createMenuItem(restaurantId, menuItem) {
        return this.post(`/restaurants/${restaurantId}/menu`, menuItem);
    }

    async updateMenuItem(restaurantId, itemId, menuItem) {
        return this.put(`/restaurants/${restaurantId}/menu/${itemId}`, menuItem);
    }

    async deleteMenuItem(restaurantId, itemId) {
        return this.delete(`/restaurants/${restaurantId}/menu/${itemId}`);
    }

    // Categories
    async getCategories(restaurantId) {
        return this.get(`/restaurants/${restaurantId}/categories`);
    }

    async createCategory(restaurantId, category) {
        return this.post(`/restaurants/${restaurantId}/categories`, category);
    }
}

export default new RestaurantApi();

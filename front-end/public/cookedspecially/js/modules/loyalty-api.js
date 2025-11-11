// Loyalty Service API Client

import ApiClient from './api-client.js';

class LoyaltyApi extends ApiClient {
    constructor() {
        super('loyalty');
    }

    // Loyalty Accounts
    async getLoyaltyAccount(customerId) {
        return this.get(`/loyalty/accounts/customer/${customerId}`);
    }

    async createLoyaltyAccount(customerId) {
        return this.post('/loyalty/accounts', { customer_id: customerId });
    }

    async getAccountBalance(customerId) {
        return this.get(`/loyalty/accounts/customer/${customerId}/balance`);
    }

    // Points Transactions
    async earnPoints(customerId, points, orderId, description) {
        return this.post('/loyalty/points/earn', {
            customer_id: customerId,
            points,
            order_id: orderId,
            description
        });
    }

    async redeemPoints(customerId, points, orderId, description) {
        return this.post('/loyalty/points/redeem', {
            customer_id: customerId,
            points,
            order_id: orderId,
            description
        });
    }

    async getPointsHistory(customerId, page = 0, size = 20) {
        return this.get(`/loyalty/points/customer/${customerId}?page=${page}&size=${size}`);
    }

    // Tiers
    async getTiers() {
        return this.get('/loyalty/tiers');
    }

    async createTier(tierData) {
        return this.post('/loyalty/tiers', tierData);
    }

    async updateTier(id, tierData) {
        return this.put(`/loyalty/tiers/${id}`, tierData);
    }

    async deleteTier(id) {
        return this.delete(`/loyalty/tiers/${id}`);
    }

    // Rewards
    async getRewards() {
        return this.get('/loyalty/rewards');
    }

    async getAvailableRewards(customerId) {
        return this.get(`/loyalty/rewards/available?customerId=${customerId}`);
    }

    async createReward(rewardData) {
        return this.post('/loyalty/rewards', rewardData);
    }

    async updateReward(id, rewardData) {
        return this.put(`/loyalty/rewards/${id}`, rewardData);
    }

    async deleteReward(id) {
        return this.delete(`/loyalty/rewards/${id}`);
    }

    async redeemReward(customerId, rewardId) {
        return this.post('/loyalty/rewards/redeem', {
            customer_id: customerId,
            reward_id: rewardId
        });
    }

    async getRedemptionHistory(customerId, page = 0, size = 20) {
        return this.get(`/loyalty/redemptions/customer/${customerId}?page=${page}&size=${size}`);
    }
}

export default new LoyaltyApi();

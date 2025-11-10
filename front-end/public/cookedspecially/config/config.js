// Configuration for CookedSpecially Consumer Site

const config = {
    // Set to 'local' for development or 'aws' for production
    environment: 'local',

    // Local development endpoints
    local: {
        restaurant: 'http://localhost:8081/api/v1',
        order: 'http://localhost:8082/api/v1',
        payment: 'http://localhost:8083/api/v1',
        customer: 'http://localhost:8084/api/v1',
        notification: 'http://localhost:8085/api/v1',
        loyalty: 'http://localhost:8086/api/v1'
    },

    // AWS production endpoints
    aws: {
        restaurant: 'https://restaurant-api.cookedspecially.com/api/v1',
        order: 'https://order-api.cookedspecially.com/api/v1',
        payment: 'https://payment-api.cookedspecially.com/api/v1',
        customer: 'https://customer-api.cookedspecially.com/api/v1',
        notification: 'https://notification-api.cookedspecially.com/api/v1',
        loyalty: 'https://loyalty-api.cookedspecially.com/api/v1'
    },

    // Get the current environment's endpoints
    getEndpoints() {
        return this[this.environment];
    },

    // API settings
    api: {
        timeout: 30000, // 30 seconds
        retries: 3
    },

    // Application settings
    app: {
        name: 'CookedSpecially',
        currency: 'INR',
        defaultRestaurantId: 1,
        itemsPerPage: 12,
        deliveryFee: 50,
        minOrderAmount: 199
    },

    // Storage keys
    storage: {
        token: 'cookedspecially_token',
        cart: 'cookedspecially_cart',
        user: 'cookedspecially_user',
        addresses: 'cookedspecially_addresses'
    }
};

export default config;

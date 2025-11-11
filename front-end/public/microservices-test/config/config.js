// Configuration for Microservices
// This file can be customized for local development or AWS deployment

const config = {
    // Environment: 'local', 'development', 'production'
    environment: 'local',

    // API Base URLs
    services: {
        restaurant: {
            baseUrl: 'http://localhost:8081/api/v1',
            healthCheck: '/actuator/health'
        },
        order: {
            baseUrl: 'http://localhost:8082/api/v1',
            healthCheck: '/actuator/health'
        },
        payment: {
            baseUrl: 'http://localhost:8083/api/v1',
            healthCheck: '/actuator/health'
        },
        customer: {
            baseUrl: 'http://localhost:8084/api/v1',
            healthCheck: '/actuator/health'
        },
        notification: {
            baseUrl: 'http://localhost:8085/api/v1',
            healthCheck: '/actuator/health'
        },
        loyalty: {
            baseUrl: 'http://localhost:8086/api/v1',
            healthCheck: '/actuator/health'
        },
        kitchen: {
            baseUrl: 'http://localhost:8087/api/v1',
            healthCheck: '/actuator/health'
        },
        reporting: {
            baseUrl: 'http://localhost:8088/api/v1',
            healthCheck: '/actuator/health'
        },
        integration: {
            baseUrl: 'http://localhost:8089/api/v1',
            healthCheck: '/actuator/health'
        },
        admin: {
            baseUrl: 'http://localhost:8090/api/v1',
            healthCheck: '/actuator/health'
        }
    },

    // AWS Configuration (override for production)
    aws: {
        services: {
            restaurant: 'https://restaurant-api.cookedspecially.com/api/v1',
            order: 'https://order-api.cookedspecially.com/api/v1',
            payment: 'https://payment-api.cookedspecially.com/api/v1',
            customer: 'https://customer-api.cookedspecially.com/api/v1',
            notification: 'https://notification-api.cookedspecially.com/api/v1',
            loyalty: 'https://loyalty-api.cookedspecially.com/api/v1',
            kitchen: 'https://kitchen-api.cookedspecially.com/api/v1',
            reporting: 'https://reporting-api.cookedspecially.com/api/v1',
            integration: 'https://integration-api.cookedspecially.com/api/v1',
            admin: 'https://admin-api.cookedspecially.com/api/v1'
        }
    },

    // Request Configuration
    request: {
        timeout: 30000, // 30 seconds
        retries: 3,
        retryDelay: 1000 // 1 second
    },

    // Authentication
    auth: {
        tokenKey: 'auth_token',
        refreshTokenKey: 'refresh_token',
        userKey: 'current_user'
    },

    // Feature Flags
    features: {
        enableAuth: true,
        enableCaching: true,
        enableMetrics: true,
        debugMode: true
    }
};

// Load environment-specific overrides
if (config.environment === 'production') {
    Object.keys(config.services).forEach(service => {
        config.services[service].baseUrl = config.aws.services[service];
    });
}

// Export configuration
export default config;

// API Client Base Module
// Provides common HTTP methods and error handling

import config from '../../config/config.js';

class ApiClient {
    constructor(serviceName) {
        this.serviceName = serviceName;
        this.baseUrl = config.services[serviceName]?.baseUrl;

        if (!this.baseUrl) {
            throw new Error(`Service ${serviceName} not configured`);
        }
    }

    // Get auth token from storage
    getAuthToken() {
        return localStorage.getItem(config.auth.tokenKey);
    }

    // Build headers for request
    buildHeaders(customHeaders = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...customHeaders
        };

        const token = this.getAuthToken();
        if (token && config.features.enableAuth) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        return headers;
    }

    // Build full URL
    buildUrl(endpoint) {
        return `${this.baseUrl}${endpoint}`;
    }

    // Generic request method
    async request(method, endpoint, options = {}) {
        const url = this.buildUrl(endpoint);
        const { body, headers = {}, timeout = config.request.timeout } = options;

        const requestConfig = {
            method,
            headers: this.buildHeaders(headers)
        };

        if (body && method !== 'GET') {
            requestConfig.body = JSON.stringify(body);
        }

        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), timeout);

            const response = await fetch(url, {
                ...requestConfig,
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            // Parse response
            const contentType = response.headers.get('content-type');
            let data;

            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                throw {
                    status: response.status,
                    statusText: response.statusText,
                    data
                };
            }

            return {
                success: true,
                data,
                status: response.status
            };

        } catch (error) {
            console.error(`API Error [${method} ${url}]:`, error);

            if (error.name === 'AbortError') {
                return {
                    success: false,
                    error: 'Request timeout',
                    status: 408
                };
            }

            return {
                success: false,
                error: error.data || error.message || 'Network error',
                status: error.status || 500
            };
        }
    }

    // HTTP Methods
    async get(endpoint, options = {}) {
        return this.request('GET', endpoint, options);
    }

    async post(endpoint, body, options = {}) {
        return this.request('POST', endpoint, { ...options, body });
    }

    async put(endpoint, body, options = {}) {
        return this.request('PUT', endpoint, { ...options, body });
    }

    async patch(endpoint, body, options = {}) {
        return this.request('PATCH', endpoint, { ...options, body });
    }

    async delete(endpoint, options = {}) {
        return this.request('DELETE', endpoint, options);
    }

    // Health check
    async healthCheck() {
        const healthUrl = config.services[this.serviceName].healthCheck;
        return this.get(healthUrl);
    }
}

export default ApiClient;

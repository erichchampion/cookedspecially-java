// Dashboard Controller
// Handles service health checks and status updates

import config from '../config/config.js';

class Dashboard {
    constructor() {
        this.services = [
            'restaurant',
            'order',
            'customer',
            'admin',
            'payment',
            'loyalty',
            'kitchen',
            'reporting',
            'integration',
            'notification'
        ];
        this.healthCheckResults = {};
    }

    async init() {
        // Set up event listeners
        const testAllButton = document.getElementById('test-all-services');
        if (testAllButton) {
            testAllButton.addEventListener('click', () => this.testAllServices());
        }

        // Run initial health checks
        await this.testAllServices();

        // Set up auto-refresh every 30 seconds
        setInterval(() => this.testAllServices(), 30000);
    }

    async testAllServices() {
        const button = document.getElementById('test-all-services');
        if (button) {
            button.disabled = true;
            button.textContent = 'Testing...';
        }

        // Test all services in parallel
        const promises = this.services.map(service => this.checkServiceHealth(service));
        await Promise.all(promises);

        // Update summary
        this.updateSummary();

        if (button) {
            button.disabled = false;
            button.textContent = 'Test All Services';
        }
    }

    async checkServiceHealth(serviceName) {
        const statusElement = document.getElementById(`status-${serviceName}`);
        const serviceCard = document.querySelector(`[data-service="${serviceName}"]`);

        if (!statusElement) return;

        // Update status to checking
        statusElement.textContent = 'Checking...';
        statusElement.className = 'service-status checking';

        try {
            const serviceConfig = config.services[serviceName];
            if (!serviceConfig) {
                throw new Error('Service not configured');
            }

            const healthUrl = `${serviceConfig.baseUrl}${serviceConfig.healthCheck}`;
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 5000);

            const response = await fetch(healthUrl, {
                method: 'GET',
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (response.ok) {
                const data = await response.json();
                this.healthCheckResults[serviceName] = {
                    status: 'up',
                    data,
                    timestamp: new Date()
                };

                statusElement.textContent = '✓ Online';
                statusElement.className = 'service-status online';
                if (serviceCard) {
                    serviceCard.classList.remove('offline');
                    serviceCard.classList.add('online');
                }
            } else {
                throw new Error(`HTTP ${response.status}`);
            }

        } catch (error) {
            this.healthCheckResults[serviceName] = {
                status: 'down',
                error: error.message,
                timestamp: new Date()
            };

            statusElement.textContent = '✗ Offline';
            statusElement.className = 'service-status offline';
            if (serviceCard) {
                serviceCard.classList.remove('online');
                serviceCard.classList.add('offline');
            }
        }
    }

    updateSummary() {
        const summaryElement = document.getElementById('status-summary');
        if (!summaryElement) return;

        const online = Object.values(this.healthCheckResults).filter(r => r.status === 'up').length;
        const total = this.services.length;
        const percentage = Math.round((online / total) * 100);

        let statusClass = 'success';
        if (percentage < 50) {
            statusClass = 'error';
        } else if (percentage < 100) {
            statusClass = 'warning';
        }

        summaryElement.innerHTML = `
            <strong>${online}/${total}</strong> services online (${percentage}%)
        `;
        summaryElement.className = `status-summary ${statusClass}`;
    }

    getHealthStatus() {
        return this.healthCheckResults;
    }
}

// Initialize dashboard when DOM is ready
const dashboard = new Dashboard();

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => dashboard.init());
} else {
    dashboard.init();
}

export default dashboard;

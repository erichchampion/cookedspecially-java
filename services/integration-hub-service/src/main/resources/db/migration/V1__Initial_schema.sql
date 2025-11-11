-- Integration Hub Service - Initial Schema

-- Social Connectors Table
CREATE TABLE social_connectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    token_expires_at DATETIME,
    configuration TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    last_sync_at DATETIME,
    last_error TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_social_connectors_restaurant (restaurant_id),
    INDEX idx_social_connectors_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Integration Configs Table
CREATE TABLE integration_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    partner_id VARCHAR(100) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    api_key VARCHAR(500),
    api_secret VARCHAR(500),
    webhook_url VARCHAR(500),
    webhook_secret VARCHAR(500),
    configuration TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    last_health_check_at DATETIME,
    last_error TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_integration_configs_restaurant (restaurant_id),
    INDEX idx_integration_configs_type (type),
    INDEX idx_integration_configs_partner (partner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Webhook Logs Table
CREATE TABLE webhook_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id VARCHAR(100) NOT NULL,
    webhook_type VARCHAR(50) NOT NULL,
    external_order_id VARCHAR(100),
    request_payload TEXT,
    request_headers TEXT,
    response_payload TEXT,
    response_status INT,
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    error_message TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    next_retry_at DATETIME,
    processing_time_ms BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_webhook_logs_partner_id (partner_id),
    INDEX idx_webhook_logs_status (status),
    INDEX idx_webhook_logs_created_at (created_at),
    INDEX idx_webhook_logs_external_order_id (external_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

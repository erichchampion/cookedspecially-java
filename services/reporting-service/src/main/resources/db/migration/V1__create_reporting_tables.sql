-- Create scheduled_reports table
CREATE TABLE scheduled_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_name VARCHAR(255) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    format VARCHAR(20) NOT NULL,
    recipient_emails TEXT NOT NULL,
    restaurant_id BIGINT,
    fulfillment_center_id BIGINT,
    parameters TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_run_at TIMESTAMP NULL,
    next_run_at TIMESTAMP NULL,
    INDEX idx_active (active),
    INDEX idx_restaurant_id (restaurant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create report_execution_history table
CREATE TABLE report_execution_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheduled_report_id BIGINT,
    report_type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    execution_duration_ms BIGINT,
    file_path VARCHAR(500),
    file_size_bytes BIGINT,
    s3_url VARCHAR(1000),
    parameters TEXT,
    error_message TEXT,
    row_count INT,
    generated_by VARCHAR(255),
    INDEX idx_scheduled_report_id (scheduled_report_id),
    INDEX idx_report_type (report_type),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    FOREIGN KEY (scheduled_report_id) REFERENCES scheduled_reports(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

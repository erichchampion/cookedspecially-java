-- Kitchen Service Database Initialization Script
-- Creates the kitchen operations database and grants permissions

CREATE DATABASE IF NOT EXISTS cookedspecially_kitchen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cookedspecially_kitchen;

-- Grant permissions to application user
GRANT ALL PRIVILEGES ON cookedspecially_kitchen.* TO 'cookeduser'@'%';
FLUSH PRIVILEGES;

-- Tables will be auto-created by Hibernate based on JPA entities
-- This script just ensures the database exists with proper encoding

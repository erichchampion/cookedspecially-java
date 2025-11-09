package com.cookedspecially.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Order Service Application
 *
 * Microservice responsible for order management, tracking, and fulfillment.
 * Extracted from monolithic CookedSpecially application as part of Phase 3.
 *
 * Key Responsibilities:
 * - Order creation and management
 * - Order status tracking
 * - Order fulfillment workflow
 * - Real-time order updates (DynamoDB)
 * - Event publishing (SNS/SQS)
 *
 * @author CookedSpecially Engineering Team
 * @version 1.0.0
 * @since 2025-11-07
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

package com.cookedspecially.restaurantservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Async Configuration for event publishing
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
}

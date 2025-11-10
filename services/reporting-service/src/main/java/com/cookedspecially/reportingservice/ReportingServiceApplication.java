package com.cookedspecially.reportingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Reporting Service.
 * Provides business analytics and reporting capabilities for sales, customer,
 * product, and operations data.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ReportingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingServiceApplication.class, args);
    }
}

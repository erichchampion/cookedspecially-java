package com.cookedspecially.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Payment Service Application
 *
 * Microservice for managing payments, refunds, and payment methods
 * in the CookedSpecially food delivery platform.
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}

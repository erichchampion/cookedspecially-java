package com.cookedspecially.restaurantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Restaurant Service Application
 *
 * Microservice for managing restaurants, menus, and menu items
 * in the CookedSpecially food delivery platform.
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}

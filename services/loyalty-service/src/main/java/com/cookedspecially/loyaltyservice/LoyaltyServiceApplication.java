package com.cookedspecially.loyaltyservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(info = @Info(
        title = "Loyalty & Promotions Service API",
        version = "1.0",
        description = "APIs for managing coupons, gift cards, promotions and loyalty points"
))
public class LoyaltyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyServiceApplication.class, args);
    }
}

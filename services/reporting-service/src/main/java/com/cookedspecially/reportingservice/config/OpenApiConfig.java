package com.cookedspecially.reportingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Reporting Service API")
                .description("Business analytics and reporting service for sales, customer, product, and operations reports")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CookedSpecially Team")
                    .email("support@cookedspecially.com")));
    }
}

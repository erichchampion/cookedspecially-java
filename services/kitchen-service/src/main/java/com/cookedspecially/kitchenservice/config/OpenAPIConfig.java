package com.cookedspecially.kitchenservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI kitchenServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Kitchen Operations Service API")
                .description("RESTful API for kitchen operations, cash register management, and delivery operations")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CookedSpecially Team")
                    .email("support@cookedspecially.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://cookedspecially.com")))
            .servers(List.of(
                new Server().url("http://localhost:8087").description("Local Development"),
                new Server().url("https://api-dev.cookedspecially.com").description("Development"),
                new Server().url("https://api.cookedspecially.com").description("Production")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token from AWS Cognito")));
    }
}

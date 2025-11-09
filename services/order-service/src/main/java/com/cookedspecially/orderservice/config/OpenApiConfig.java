package com.cookedspecially.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) Configuration
 *
 * Configures API documentation for the Order Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    /**
     * OpenAPI specification
     */
    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CookedSpecially Order Service API")
                .description("Microservice for managing orders in the CookedSpecially food delivery platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CookedSpecially Team")
                    .email("support@cookedspecially.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development"),
                new Server()
                    .url("https://api-dev.cookedspecially.com")
                    .description("Development Environment"),
                new Server()
                    .url("https://api.cookedspecially.com")
                    .description("Production Environment")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from authentication service")));
    }
}

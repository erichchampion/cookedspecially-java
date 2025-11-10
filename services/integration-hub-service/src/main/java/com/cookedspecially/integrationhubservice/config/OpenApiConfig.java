package com.cookedspecially.integrationhubservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI integrationHubServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Integration Hub Service API")
                        .description("Third-party integration hub for Zomato, social media, and partner platforms")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

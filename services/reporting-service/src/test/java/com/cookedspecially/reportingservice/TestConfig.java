package com.cookedspecially.reportingservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

import static org.mockito.Mockito.mock;

/**
 * Test configuration for mocking AWS services and security.
 */
@TestConfiguration
@EnableWebSecurity
public class TestConfig {

    /**
     * Disable security for integration tests.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Mock S3 client for tests.
     */
    @Bean
    @Primary
    public S3Client mockS3Client() {
        return mock(S3Client.class);
    }

    /**
     * Mock SES client for tests.
     */
    @Bean
    @Primary
    public SesClient mockSesClient() {
        return mock(SesClient.class);
    }
}

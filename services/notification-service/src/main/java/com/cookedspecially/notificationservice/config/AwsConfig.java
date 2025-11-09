package com.cookedspecially.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * AWS Configuration
 */
@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    /**
     * SES Client for email notifications
     */
    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    /**
     * SNS Client for SMS and Push notifications
     */
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    /**
     * SQS Client for consuming events
     */
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}

package com.cookedspecially.restaurantservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
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
     * SNS Client for publishing events
     */
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    /**
     * SQS Client for consuming messages
     */
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    /**
     * S3 Client for image storage
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    /**
     * S3 Presigner for generating pre-signed URLs
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}

package com.cookedspecially.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

/**
 * AWS SDK Configuration
 *
 * Configures AWS SDK v2 clients for SNS, SQS, DynamoDB, and S3
 */
@Configuration
public class AwsConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.endpoint-override:#{null}}")
    private String endpointOverride;

    /**
     * SNS Client for publishing events
     */
    @Bean
    public SnsClient snsClient() {
        software.amazon.awssdk.services.sns.SnsClientBuilder builder = SnsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create());

        if (endpointOverride != null && !endpointOverride.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }

    /**
     * SQS Client for consuming messages
     */
    @Bean
    public SqsClient sqsClient() {
        software.amazon.awssdk.services.sqs.SqsClientBuilder builder = SqsClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create());

        if (endpointOverride != null && !endpointOverride.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }

    /**
     * DynamoDB Client for payment transactions
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create());

        if (endpointOverride != null && !endpointOverride.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }

    /**
     * S3 Client for file storage
     */
    @Bean
    public S3Client s3Client() {
        software.amazon.awssdk.services.s3.S3ClientBuilder builder = S3Client.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create());

        if (endpointOverride != null && !endpointOverride.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }
}

package com.cookedspecially.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

/**
 * Service for storing and retrieving reports from S3.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.reports}")
    private String bucketName;

    /**
     * Upload report to S3 and return presigned URL.
     */
    public String uploadReport(String fileName, byte[] content) {
        log.info("Uploading report to S3: {}", fileName);

        String key = "reports/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(getContentType(fileName))
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

        // Generate presigned URL valid for 7 days
        return generatePresignedUrl(key, Duration.ofDays(7));
    }

    /**
     * Generate presigned URL for downloading report.
     */
    public String generatePresignedUrl(String key, Duration duration) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(builder -> builder
                    .bucket(bucketName)
                    .key(key))
                .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectPresignRequest);
            return presignedRequest.url().toString();
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".csv")) {
            return "text/csv";
        }
        return "application/octet-stream";
    }
}

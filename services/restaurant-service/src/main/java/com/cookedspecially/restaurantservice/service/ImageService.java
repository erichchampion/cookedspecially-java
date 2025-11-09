package com.cookedspecially.restaurantservice.service;

import com.cookedspecially.restaurantservice.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

/**
 * Image Service for S3 integration
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.restaurant-images-prefix:restaurant-images/}")
    private String restaurantImagesPrefix;

    @Value("${aws.s3.menu-item-images-prefix:menu-item-images/}")
    private String menuItemImagesPrefix;

    @Value("${aws.s3.presigned-url-duration-minutes:60}")
    private long presignedUrlDurationMinutes;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    /**
     * Upload restaurant image to S3
     */
    public String uploadRestaurantImage(MultipartFile file, Long restaurantId) {
        log.info("Uploading restaurant image for restaurant: {}", restaurantId);

        validateFile(file);

        String fileName = generateFileName(restaurantImagesPrefix, restaurantId, file.getOriginalFilename());
        String imageUrl = uploadToS3(file, fileName);

        log.info("Uploaded restaurant image: {}", imageUrl);
        return imageUrl;
    }

    /**
     * Upload menu item image to S3
     */
    public String uploadMenuItemImage(MultipartFile file, Long menuItemId) {
        log.info("Uploading menu item image for item: {}", menuItemId);

        validateFile(file);

        String fileName = generateFileName(menuItemImagesPrefix, menuItemId, file.getOriginalFilename());
        String imageUrl = uploadToS3(file, fileName);

        log.info("Uploaded menu item image: {}", imageUrl);
        return imageUrl;
    }

    /**
     * Delete image from S3
     */
    public void deleteImage(String imageUrl) {
        try {
            log.info("Deleting image: {}", imageUrl);

            String key = extractKeyFromUrl(imageUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            s3Client.deleteObject(deleteRequest);

            log.info("Deleted image: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete image: {}", imageUrl, e);
            throw new ImageUploadException(imageUrl, "Failed to delete image: " + e.getMessage());
        }
    }

    /**
     * Generate pre-signed URL for image access
     */
    public String generatePresignedUrl(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlDurationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for: {}", imageUrl, e);
            throw new ImageUploadException(imageUrl, "Failed to generate presigned URL: " + e.getMessage());
        }
    }

    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("file", "File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageUploadException(file.getOriginalFilename(),
                "File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new ImageUploadException(file.getOriginalFilename(),
                "Invalid file type. Allowed types: JPEG, PNG, GIF, WebP");
        }
    }

    /**
     * Check if content type is allowed
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate unique file name
     */
    private String generateFileName(String prefix, Long resourceId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s%d/%s%s", prefix, resourceId, uniqueId, extension);
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    /**
     * Upload file to S3
     */
    private String uploadToS3(MultipartFile file, String key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Return S3 URL
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
        } catch (IOException e) {
            log.error("Failed to read file: {}", file.getOriginalFilename(), e);
            throw new ImageUploadException(file.getOriginalFilename(), e);
        } catch (S3Exception e) {
            log.error("Failed to upload to S3: {}", key, e);
            throw new ImageUploadException(file.getOriginalFilename(),
                "S3 upload failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Extract S3 key from full URL
     */
    private String extractKeyFromUrl(String imageUrl) {
        // Format: https://bucket-name.s3.amazonaws.com/key
        // or https://bucket-name.s3.region.amazonaws.com/key
        try {
            String[] parts = imageUrl.split(".s3", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid S3 URL format");
            }

            String keyPart = parts[1];
            // Remove .amazonaws.com/ or .region.amazonaws.com/
            int slashIndex = keyPart.indexOf('/', keyPart.indexOf("amazonaws.com"));
            if (slashIndex == -1) {
                throw new IllegalArgumentException("Invalid S3 URL format");
            }

            return keyPart.substring(slashIndex + 1);
        } catch (Exception e) {
            log.error("Failed to extract key from URL: {}", imageUrl, e);
            throw new ImageUploadException(imageUrl, "Invalid S3 URL format");
        }
    }

    /**
     * Check if file exists in S3
     */
    public boolean imageExists(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking if image exists: {}", imageUrl, e);
            return false;
        }
    }
}

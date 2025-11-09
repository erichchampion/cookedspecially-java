package com.cookedspecially.restaurantservice.controller;

import com.cookedspecially.restaurantservice.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Image Controller
 */
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image", description = "Image upload and management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class ImageController {

    private final ImageService imageService;

    /**
     * Upload restaurant image
     */
    @PostMapping(value = "/restaurants/{restaurantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Upload restaurant image", description = "Upload image for restaurant (owner only)")
    public ResponseEntity<Map<String, String>> uploadRestaurantImage(
        @PathVariable Long restaurantId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("Uploading image for restaurant: {}", restaurantId);

        String imageUrl = imageService.uploadRestaurantImage(file, restaurantId);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("message", "Image uploaded successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload menu item image
     */
    @PostMapping(value = "/menu-items/{menuItemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Upload menu item image", description = "Upload image for menu item (owner only)")
    public ResponseEntity<Map<String, String>> uploadMenuItemImage(
        @PathVariable Long menuItemId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("Uploading image for menu item: {}", menuItemId);

        String imageUrl = imageService.uploadMenuItemImage(file, menuItemId);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("message", "Image uploaded successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete image
     */
    @DeleteMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Delete image", description = "Delete image from S3 (owner only)")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestParam String imageUrl) {
        log.info("Deleting image: {}", imageUrl);

        imageService.deleteImage(imageUrl);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Image deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Generate pre-signed URL
     */
    @GetMapping("/presigned-url")
    @Operation(summary = "Generate pre-signed URL", description = "Generate pre-signed URL for image access")
    public ResponseEntity<Map<String, String>> generatePresignedUrl(@RequestParam String imageUrl) {
        log.info("Generating pre-signed URL for: {}", imageUrl);

        String presignedUrl = imageService.generatePresignedUrl(imageUrl);

        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);

        return ResponseEntity.ok(response);
    }

    /**
     * Check if image exists
     */
    @GetMapping("/exists")
    @Operation(summary = "Check if image exists", description = "Check if image exists in S3")
    public ResponseEntity<Map<String, Boolean>> checkImageExists(@RequestParam String imageUrl) {
        log.info("Checking if image exists: {}", imageUrl);

        boolean exists = imageService.imageExists(imageUrl);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}

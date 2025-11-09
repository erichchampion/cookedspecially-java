package com.cookedspecially.restaurantservice.exception;

/**
 * Exception thrown when image upload fails
 */
public class ImageUploadException extends RuntimeException {
    private final String fileName;

    public ImageUploadException(String fileName, String message) {
        super("Failed to upload image " + fileName + ": " + message);
        this.fileName = fileName;
    }

    public ImageUploadException(String fileName, Throwable cause) {
        super("Failed to upload image " + fileName, cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

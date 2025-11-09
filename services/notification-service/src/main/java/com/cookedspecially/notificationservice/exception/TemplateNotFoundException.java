package com.cookedspecially.notificationservice.exception;

/**
 * Exception thrown when template is not found
 */
public class TemplateNotFoundException extends RuntimeException {
    private final String templateKey;

    public TemplateNotFoundException(String templateKey) {
        super("Template not found: " + templateKey);
        this.templateKey = templateKey;
    }

    public String getTemplateKey() {
        return templateKey;
    }
}

package com.cookedspecially.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationTemplate;
import com.cookedspecially.notificationservice.domain.NotificationType;
import com.cookedspecially.notificationservice.exception.TemplateNotFoundException;
import com.cookedspecially.notificationservice.repository.NotificationTemplateRepository;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Template Service for processing notification templates
 */
@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final NotificationTemplateRepository templateRepository;

    // Constructor
    public TemplateService(NotificationTemplateRepository templateRepository, TemplateEngine templateEngine) {
        this.templateRepository = templateRepository;
        this.templateEngine = templateEngine;
    }
    private final TemplateEngine templateEngine;

    /**
     * Process template with variables
     */
    @Cacheable(value = "templates", key = "#templateKey + '_' + #variables.hashCode()")
    public String processTemplate(String templateKey, Map<String, Object> variables) {
        log.debug("Processing template: {}", templateKey);

        NotificationTemplate template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow(() -> new TemplateNotFoundException(templateKey));

        if (!template.isIsActive()) {
            throw new TemplateNotFoundException(templateKey + " (inactive)");
        }

        // Use simple string substitution for non-HTML templates
        if (template.getHtmlTemplate() == null || template.getHtmlTemplate().isEmpty()) {
            return processSimpleTemplate(template.getBodyTemplate(), variables);
        }

        // Use Thymeleaf for HTML templates
        return processHtmlTemplate(template.getBodyTemplate(), variables);
    }

    /**
     * Process template by type and channel
     */
    public String processTemplate(NotificationType type, NotificationChannel channel,
                                   Map<String, Object> variables) {
        log.debug("Processing template for type: {}, channel: {}", type, channel);

        NotificationTemplate template = templateRepository
            .findByTypeAndChannelAndIsActiveTrue(type, channel)
            .orElseThrow(() -> new TemplateNotFoundException(
                String.format("%s_%s", type, channel)));

        return processTemplate(template.getTemplateKey(), variables);
    }

    /**
     * Get processed subject
     */
    public String processSubject(String templateKey, Map<String, Object> variables) {
        log.debug("Processing subject for template: {}", templateKey);

        NotificationTemplate template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow(() -> new TemplateNotFoundException(templateKey));

        return processSimpleTemplate(template.getSubject(), variables);
    }

    /**
     * Process simple template (non-HTML) with variable substitution
     */
    private String processSimpleTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        // Convert variables to String map for StringSubstitutor
        Map<String, String> stringVariables = new java.util.HashMap<>();
        variables.forEach((key, value) ->
            stringVariables.put(key, value != null ? value.toString() : ""));

        StringSubstitutor substitutor = new StringSubstitutor(stringVariables);
        substitutor.setVariablePrefix("${");
        substitutor.setVariableSuffix("}");

        return substitutor.replace(template);
    }

    /**
     * Process HTML template using Thymeleaf
     */
    private String processHtmlTemplate(String templateContent, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process(templateContent, context);
    }

    /**
     * Get template by key
     */
    @Cacheable(value = "templates", key = "#templateKey")
    public NotificationTemplate getTemplate(String templateKey) {
        return templateRepository.findByTemplateKey(templateKey)
            .orElseThrow(() -> new TemplateNotFoundException(templateKey));
    }

    /**
     * Get template by type and channel
     */
    public NotificationTemplate getTemplate(NotificationType type, NotificationChannel channel) {
        return templateRepository.findByTypeAndChannelAndIsActiveTrue(type, channel)
            .orElseThrow(() -> new TemplateNotFoundException(
                String.format("%s_%s", type, channel)));
    }

    /**
     * Validate template variables
     */
    public boolean validateTemplateVariables(String templateKey, Map<String, Object> variables) {
        NotificationTemplate template = getTemplate(templateKey);

        if (template.getAvailableVariables() == null || template.getAvailableVariables().isEmpty()) {
            return true;
        }

        String[] requiredVars = template.getAvailableVariables().split(",");
        for (String var : requiredVars) {
            String trimmedVar = var.trim();
            if (!variables.containsKey(trimmedVar)) {
                log.warn("Missing required variable '{}' for template '{}'", trimmedVar, templateKey);
                return false;
            }
        }

        return true;
    }
}

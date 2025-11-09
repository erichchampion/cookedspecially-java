package com.cookedspecially.notificationservice.service;

import com.cookedspecially.notificationservice.exception.NotificationSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Email Service using AWS SES
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @Value("${aws.ses.from-name}")
    private String fromName;

    @Value("${aws.ses.reply-to-email}")
    private String replyToEmail;

    @Value("${aws.ses.configuration-set:}")
    private String configurationSet;

    /**
     * Send HTML email
     */
    public String sendHtmlEmail(String toEmail, String subject, String htmlBody, String textBody) {
        log.info("Sending HTML email to: {}", toEmail);

        try {
            SendEmailRequest request = SendEmailRequest.builder()
                .source(formatEmailAddress(fromName, fromEmail))
                .destination(Destination.builder()
                    .toAddresses(toEmail)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder().data(subject).build())
                    .body(Body.builder()
                        .html(Content.builder().data(htmlBody).build())
                        .text(Content.builder().data(textBody).build())
                        .build())
                    .build())
                .replyToAddresses(replyToEmail)
                .configurationSetName(configurationSet.isEmpty() ? null : configurationSet)
                .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            String messageId = response.messageId();

            log.info("Email sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (SesException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("EMAIL", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}", toEmail, e);
            throw new NotificationSendException("EMAIL", e);
        }
    }

    /**
     * Send plain text email
     */
    public String sendTextEmail(String toEmail, String subject, String textBody) {
        log.info("Sending text email to: {}", toEmail);

        try {
            SendEmailRequest request = SendEmailRequest.builder()
                .source(formatEmailAddress(fromName, fromEmail))
                .destination(Destination.builder()
                    .toAddresses(toEmail)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder().data(subject).build())
                    .body(Body.builder()
                        .text(Content.builder().data(textBody).build())
                        .build())
                    .build())
                .replyToAddresses(replyToEmail)
                .configurationSetName(configurationSet.isEmpty() ? null : configurationSet)
                .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            String messageId = response.messageId();

            log.info("Email sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (SesException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("EMAIL", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}", toEmail, e);
            throw new NotificationSendException("EMAIL", e);
        }
    }

    /**
     * Send templated email using SES template
     */
    public String sendTemplatedEmail(String toEmail, String templateName, String templateData) {
        log.info("Sending templated email to: {} using template: {}", toEmail, templateName);

        try {
            SendTemplatedEmailRequest request = SendTemplatedEmailRequest.builder()
                .source(formatEmailAddress(fromName, fromEmail))
                .destination(Destination.builder()
                    .toAddresses(toEmail)
                    .build())
                .template(templateName)
                .templateData(templateData)
                .replyToAddresses(replyToEmail)
                .configurationSetName(configurationSet.isEmpty() ? null : configurationSet)
                .build();

            SendTemplatedEmailResponse response = sesClient.sendTemplatedEmail(request);
            String messageId = response.messageId();

            log.info("Templated email sent successfully. Message ID: {}", messageId);
            return messageId;

        } catch (SesException e) {
            log.error("Failed to send templated email to {}: {}", toEmail, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("EMAIL", e);
        } catch (Exception e) {
            log.error("Unexpected error sending templated email to {}", toEmail, e);
            throw new NotificationSendException("EMAIL", e);
        }
    }

    /**
     * Verify email address
     */
    public void verifyEmailAddress(String email) {
        log.info("Verifying email address: {}", email);

        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                .emailAddress(email)
                .build();

            sesClient.verifyEmailIdentity(request);
            log.info("Verification email sent to: {}", email);

        } catch (SesException e) {
            log.error("Failed to verify email {}: {}", email, e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("EMAIL", e);
        }
    }

    /**
     * Check if email is verified
     */
    public boolean isEmailVerified(String email) {
        try {
            GetIdentityVerificationAttributesRequest request =
                GetIdentityVerificationAttributesRequest.builder()
                    .identities(email)
                    .build();

            GetIdentityVerificationAttributesResponse response =
                sesClient.getIdentityVerificationAttributes(request);

            IdentityVerificationAttributes attributes =
                response.verificationAttributes().get(email);

            if (attributes != null) {
                return attributes.verificationStatus() == VerificationStatus.SUCCESS;
            }

            return false;

        } catch (SesException e) {
            log.error("Failed to check email verification for {}: {}", email, e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    /**
     * Format email address with display name
     */
    private String formatEmailAddress(String name, String email) {
        if (name == null || name.isEmpty()) {
            return email;
        }
        return String.format("%s <%s>", name, email);
    }

    /**
     * Get send quota
     */
    public SendQuota getSendQuota() {
        try {
            GetSendQuotaResponse response = sesClient.getSendQuota();

            return SendQuota.builder()
                .max24HourSend(response.max24HourSend())
                .maxSendRate(response.maxSendRate())
                .sentLast24Hours(response.sentLast24Hours())
                .build();

        } catch (SesException e) {
            log.error("Failed to get send quota: {}", e.awsErrorDetails().errorMessage());
            throw new NotificationSendException("EMAIL", e);
        }
    }
}

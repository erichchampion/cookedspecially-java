package com.cookedspecially.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Service for sending emails via AWS SES.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    /**
     * Send report email with download link.
     */
    public void sendReportEmail(String[] recipients, String reportName, String downloadUrl) {
        log.info("Sending report email to {} recipients", recipients.length);

        String subject = "Your Scheduled Report: " + reportName;
        String body = buildEmailBody(reportName, downloadUrl);

        try {
            SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder()
                    .toAddresses(recipients)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder()
                        .data(subject)
                        .build())
                    .body(Body.builder()
                        .html(Content.builder()
                            .data(body)
                            .build())
                        .build())
                    .build())
                .source(fromEmail)
                .build();

            sesClient.sendEmail(request);
            log.info("Email sent successfully");

        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
        }
    }

    private String buildEmailBody(String reportName, String downloadUrl) {
        return """
            <html>
            <body>
                <h2>Your Scheduled Report is Ready</h2>
                <p>The scheduled report <strong>%s</strong> has been generated successfully.</p>
                <p>You can download it using the link below:</p>
                <p><a href="%s">Download Report</a></p>
                <p>This link will expire in 7 days.</p>
                <br/>
                <p>Thank you,<br/>CookedSpecially Reporting Team</p>
            </body>
            </html>
            """.formatted(reportName, downloadUrl);
    }
}

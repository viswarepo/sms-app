package com.sms.notification.service;

import com.sms.notification.dto.SendEmailRequest;
import com.sms.notification.dto.SendSmsRequest;
import com.sms.notification.entity.NotificationChannel;
import com.sms.notification.entity.NotificationLog;
import com.sms.notification.entity.NotificationStatus;
import com.sms.notification.entity.NotificationTemplate;
import com.sms.notification.exception.InvalidRequestException;
import com.sms.notification.exception.ResourceNotFoundException;
import com.sms.notification.repository.NotificationLogRepository;
import com.sms.notification.util.TemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final MailgunService mailgunService;
    private final TwilioService twilioService;
    private final TemplateService templateService;
    private final NotificationLogRepository notificationLogRepository;

    @Transactional
    public NotificationLog sendEmail(SendEmailRequest request, String organizationId) {
        String subject;
        String body;

        if (request.getTemplateCode() != null) {
            NotificationTemplate template = templateService.getByCodeAndOrganizationId(request.getTemplateCode(), organizationId);
            if (template.getChannel() != NotificationChannel.EMAIL) {
                throw new InvalidRequestException(
                        "Template " + request.getTemplateCode() + " is not an EMAIL template");
            }
            subject = TemplateRenderer.render(template.getSubject(), request.getVariables());
            body = TemplateRenderer.render(template.getBody(), request.getVariables());
        } else {
            if (request.getSubject() == null || request.getBody() == null) {
                throw new InvalidRequestException(
                        "Either templateCode or both subject and body must be provided");
            }
            subject = request.getSubject();
            body = request.getBody();
        }

        NotificationLog logEntry = NotificationLog.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient(request.getTo())
                .organizationId(organizationId)
                .templateCode(request.getTemplateCode())
                .subject(subject)
                .body(body)
                .status(NotificationStatus.PENDING)
                .attempts(1)
                .build();
        logEntry = notificationLogRepository.save(logEntry);

        try {
            String messageId = mailgunService.sendEmail(request.getTo(), subject, body);
            logEntry.setStatus(NotificationStatus.SENT);
            logEntry.setProviderMessageId(messageId);
        } catch (Exception e) {
            log.error("Failed to send email notification {}", logEntry.getId(), e);
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
        }

        return notificationLogRepository.save(logEntry);
    }

    @Transactional
    public NotificationLog sendSms(SendSmsRequest request, String organizationId) {
        String message;

        if (request.getTemplateCode() != null) {
            NotificationTemplate template = templateService.getByCodeAndOrganizationId(request.getTemplateCode(), organizationId);
            if (template.getChannel() != NotificationChannel.SMS) {
                throw new InvalidRequestException(
                        "Template " + request.getTemplateCode() + " is not an SMS template");
            }
            message = TemplateRenderer.render(template.getBody(), request.getVariables());
        } else {
            if (request.getMessage() == null) {
                throw new InvalidRequestException("Either templateCode or message must be provided");
            }
            message = request.getMessage();
        }

        NotificationLog logEntry = NotificationLog.builder()
                .channel(NotificationChannel.SMS)
                .recipient(request.getTo())
                .templateCode(request.getTemplateCode())
                .organizationId(organizationId)
                .body(message)
                .status(NotificationStatus.PENDING)
                .attempts(1)
                .build();
        logEntry = notificationLogRepository.save(logEntry);

        try {
            String sid = twilioService.sendSms(request.getTo(), message);
            logEntry.setStatus(NotificationStatus.SENT);
            logEntry.setProviderMessageId(sid);
        } catch (Exception e) {
            log.error("Failed to send SMS notification {}", logEntry.getId(), e);
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
        }

        return notificationLogRepository.save(logEntry);
    }

    public NotificationLog getByIdAndOrganizationid(String id, String organizationId) {
        return notificationLogRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification log not found: " + id));
    }

    @Transactional
    public void updateStatusByProviderMessageId(String providerMessageId, NotificationStatus status,
                                                String errorMessage, String organizationId) {
        notificationLogRepository.findByProviderMessageIdAndOrganizationId(providerMessageId, organizationId).ifPresentOrElse(logEntry -> {
            logEntry.setStatus(status);
            if (errorMessage != null) {
                logEntry.setErrorMessage(errorMessage);
            }
            notificationLogRepository.save(logEntry);
        }, () -> log.warn("Delivery-status webhook for unknown provider message id: {}", providerMessageId));
    }
}

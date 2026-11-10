package com.sms.notification.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.notification.dto.SendEmailRequest;
import com.sms.notification.dto.SendSmsRequest;
import com.sms.notification.entity.AlertCode;
import com.sms.notification.event.CustomerRegisteredEvent;
import com.sms.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AlertInvoiceGeneratedListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public AlertInvoiceGeneratedListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "alert.invoice.generated", groupId = "alert.notification.service")
    @Transactional
    public void onInvoiceGenerated(String rawJson) {
        CustomerRegisteredEvent event;
        try {
            event = objectMapper.readValue(rawJson, CustomerRegisteredEvent.class);
        } catch (JsonProcessingException e) {
            log.error("Could not parse CustomerRegisteredEvent, dropping message: {}", rawJson, e);
            return;
        }
        // Send welcome email
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setTemplateCode(AlertCode.USR_EMAIL_ALERT_001.getCode());
        sendEmailRequest.setBody("Hi " + event.name() + ", your account has been created successfully!");
        sendEmailRequest.setTo(event.email());

        notificationService.sendEmail(sendEmailRequest, event.organizationId());

        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setMessage("Welcome " + event.name() + "! Your account is ready.");
        smsRequest.setTo(event.mobile());
        smsRequest.setTemplateCode(AlertCode.USR_EMAIL_ALERT_001.getCode());

        // Send welcome SMS
        if (event.mobile() != null && !event.mobile().isEmpty()) {
            notificationService.sendSms(smsRequest, event.organizationId()
            );
        }
    }
}


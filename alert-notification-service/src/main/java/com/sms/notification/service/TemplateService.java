package com.sms.notification.service;

import com.sms.notification.dto.CreateTemplateRequest;
import com.sms.notification.entity.NotificationChannel;
import com.sms.notification.entity.NotificationTemplate;
import com.sms.notification.exception.InvalidRequestException;
import com.sms.notification.exception.ResourceNotFoundException;
import com.sms.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;

    @Transactional
    public NotificationTemplate createTemplate(CreateTemplateRequest request, String organizationId) {
        if (request.getChannel() == NotificationChannel.EMAIL
                && (request.getSubject() == null || request.getSubject().isBlank())) {
            throw new InvalidRequestException("Email templates require a subject");
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .code(request.getCode())
                .organizationId(organizationId)
                .channel(request.getChannel())
                .subject(request.getSubject())
                .body(request.getBody())
                .active(true)
                .build();

        return templateRepository.save(template);
    }

    public NotificationTemplate getByCodeAndOrganizationId(String code, String organizationId) {
        return templateRepository.findByCodeAndActiveTrueAndOrganizationId(code, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No active template found for code: " + code));
    }

    public List<NotificationTemplate> getByOrganizationId(String organizationId) {
        return templateRepository.findByActiveTrueAndOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No active template found for : " + organizationId));
    }
}

package com.sms.notification.controller;

import com.sms.notification.dto.CreateTemplateRequest;
import com.sms.notification.entity.AlertCode;
import com.sms.notification.entity.NotificationTemplate;
import com.sms.notification.service.TemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notification-templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/create")
    public ResponseEntity<NotificationTemplate> create(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody CreateTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request, organizationId));
    }

    @GetMapping("/{code}")
    public NotificationTemplate getByCode(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @PathVariable String code) {
        return templateService.getByCodeAndOrganizationId(code, organizationId);
    }

    @GetMapping("/all")
    public List<NotificationTemplate> listTemplates(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId) {
        return templateService.getByOrganizationId(organizationId);
    }

    @GetMapping("/alertcode")
    public List<Map<String, String>> getAlertCodes() {
        return Arrays.stream(AlertCode.values())
                .map(code -> Map.of(
                        "code", code.getCode(),
                        "description", code.getDescription(),
                        "channel", code.name().contains("EMAIL") ? "EMAIL" : "SMS"
                ))
                .collect(Collectors.toList());
    }
}

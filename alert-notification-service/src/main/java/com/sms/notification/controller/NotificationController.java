package com.sms.notification.controller;

import com.sms.notification.dto.NotificationResponse;
import com.sms.notification.dto.SendEmailRequest;
import com.sms.notification.dto.SendSmsRequest;
import com.sms.notification.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody SendEmailRequest request) {
        var log = notificationService.sendEmail(request, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(NotificationResponse.from(log));
    }

    @PostMapping("/sms")
    public ResponseEntity<NotificationResponse> sendSms(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody SendSmsRequest request) {
        var log = notificationService.sendSms(request, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(NotificationResponse.from(log));
    }

    @GetMapping("/{id}")
    public NotificationResponse get(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @PathVariable String id) {
        return NotificationResponse.from(notificationService.getByIdAndOrganizationid(id, organizationId));
    }
}

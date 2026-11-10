package com.sms.notification.dto;

import com.sms.notification.entity.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTemplateRequest {

    @NotBlank
    private String code;

    @NotNull
    private NotificationChannel channel;

    /**
     * Required for EMAIL, ignored for SMS.
     */
    private String subject;

    @NotBlank
    private String body;
}

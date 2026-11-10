package com.sms.notification.dto;

import com.sms.notification.entity.NotificationChannel;
import com.sms.notification.entity.NotificationLog;
import com.sms.notification.entity.NotificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {

    private String id;
    private NotificationChannel channel;
    private String recipient;
    private NotificationStatus status;
    private String providerMessageId;
    private String errorMessage;

    public static NotificationResponse from(NotificationLog log) {
        return NotificationResponse.builder()
                .id(log.getId())
                .channel(log.getChannel())
                .recipient(log.getRecipient())
                .status(log.getStatus())
                .providerMessageId(log.getProviderMessageId())
                .errorMessage(log.getErrorMessage())
                .build();
    }
}

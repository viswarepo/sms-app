package com.sms.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Reusable message template. Body/subject may contain {{placeholder}}
 * tokens, substituted at send time from the variables map the caller
 * supplies - see TemplateRenderer.
 */
@Entity
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "organization_id", length = 255)
    private String organizationId;


    /**
     * Stable lookup key, e.g. "payment-failed", "subscription-renewed".
     */
    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    /**
     * Only used for EMAIL; ignored for SMS. May contain {{placeholders}}.
     */
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

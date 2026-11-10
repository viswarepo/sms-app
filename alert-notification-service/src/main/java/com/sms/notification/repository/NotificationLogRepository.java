package com.sms.notification.repository;

import com.sms.notification.entity.NotificationLog;
import com.sms.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    Optional<NotificationLog> findByProviderMessageIdAndOrganizationId(String providerMessageId, String organizationId);

    List<NotificationLog> findByStatusAndOrganizationId(NotificationStatus status, String organizationId);

    List<NotificationLog> findByRecipientAndOrganizationId(String recipient, String organizationId);

    Optional<NotificationLog> findByIdAndOrganizationId(String id, String organizationId);
}

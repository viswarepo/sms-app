package com.sms.notification.repository;

import com.sms.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByCodeAndActiveTrueAndOrganizationId(String code, String organizationId);

    Optional<List<NotificationTemplate>> findByActiveTrueAndOrganizationId(String organizationId);
}

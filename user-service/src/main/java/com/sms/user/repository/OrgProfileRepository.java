package com.sms.user.repository;

import com.sms.user.domain.OrgInfo;
import com.sms.user.domain.OrganizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrgProfileRepository extends JpaRepository<OrganizationProfile, String> {
    Optional<OrganizationProfile> findByOrganizationId(String organizationId);

}

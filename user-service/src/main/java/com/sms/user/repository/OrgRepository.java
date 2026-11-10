package com.sms.user.repository;

import com.sms.user.domain.OrgInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgRepository extends JpaRepository<OrgInfo, String> {

    Optional<OrgInfo> findByEmail(String email);

    Optional<OrgInfo> findById(String id);

    Optional<OrgInfo> findByOrgname(String username);

    boolean existsByEmail(String email);

    boolean existsById(String id);

    boolean existsByOrgname(String orgname);

    void deleteById(String id);

    Page<OrgInfo> findByOrgnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);
}


package com.sms.user.repository;

import com.sms.user.domain.ERole;
import com.sms.user.domain.UserInfo;
import com.sms.user.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {

    Optional<UserInfo> findByEmail(String email);

    List<UserInfo> findByRole(ERole role);

    List<UserInfo> findByRoleAndOrgId(ERole role, String orgId);

    Optional<UserInfo> findByUsername(String username);

    boolean existsByOrgIdAndRole(String orgId, ERole role);

    Optional<UserInfo> findByUsernameAndOrgId(String username, String orgId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Page<UserInfo> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);

    @Query(value = "SELECT u.id AS id, u.username, u.password, u.email, u.full_name AS fullName, " +
            "u.role, u.org_id AS organizationId, o.org_name AS organizationName, " +
            "u.customer_id AS customerId, u.created_at AS createdAt, u.updated_at AS updatedAt " +
            "FROM user_info u " +
            "JOIN org_info o ON u.org_id = o.id " +
            "WHERE u.username = :username",
            nativeQuery = true)
    Optional<UserResponseDTO> getUserAndOrgInfo(@Param("username") String username);
}

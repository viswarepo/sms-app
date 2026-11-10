package com.sms.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "org_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = "orgname"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_type", nullable = false)
    private EOrgType orgType; // OWNER or CLIENT

    @Column(name = "org_name", length = 50)
    private String orgname;

    @Column(name ="org_email", length = 100)
    private String email;

    @Column(name ="cust_signup_url", length = 100)
    private String custSignupUrl;

    @Column(name="org_phone", length = 50)
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

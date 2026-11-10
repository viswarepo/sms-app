package com.sms.user.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name ="org_profiles")
public class OrganizationProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // auto-increment PK
    private String id;

    @Column(nullable = false, unique = true)
    private String organizationId; // FK to Customer.id

    @Column(name="org_description", length = 255)
    private String description;

    @Column(name = "org_legal_name", length = 255)
    private String legalName;

    @Column(name="org_phone", length = 50)
    private String phone;

    @Column(name="org_website", length = 200)
    private String website;

    @Column(name="org_country", length = 50)
    private String country;

    @Column(name = "org_state", length = 50)
    private String state;

    @Column(name = "org_city", length = 50)
    private String city;

    @Column(name="address1", length = 200)
    private String address1;

    @Column(name = "address2", length = 200)
    private String address2;

    @Column(name = "alter_mobile", length = 20)
    private String alterMobile;

    @Column(name = "landline_number", length = 20)
    private String landlineNumber;

    @Column(name = "preferred_currency", length = 10)
    private String preferredCurrency;

    @Column(name = "locale", length = 50)
    private String locale;

    @Column(name = "taxId", length = 50)
    private String taxId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected OrganizationProfile() {}

    public OrganizationProfile(String organizationId) {
        this.organizationId = organizationId;
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.sms.user.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class OrgProfileRequest {
    private String organizationId; // FK to Customer.id
    private String description;
    private String legalName;
    private String phone;
    private String alterMobile;
    private String landlineNumber;
    private String website;
    private String country;
    private String state;
    private String city;
    private String address1;
    private String address2;
    private String preferredCurrency;
    private String locale;
    private String taxId;
}

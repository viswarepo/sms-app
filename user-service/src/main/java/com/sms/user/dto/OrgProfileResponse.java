package com.sms.user.dto;

import com.sms.user.domain.OrgInfo;
import com.sms.user.domain.OrganizationProfile;
import lombok.Data;

@Data
public class OrgProfileResponse {
    private String id;
    private String orgname;
    private String email;
    private String mobile;
    private String orgType;

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

    // Factory method to build response from Customer + Profile
    public static OrgProfileResponse from(OrgInfo orgInfo, OrganizationProfile profile) {
        OrgProfileResponse response = new OrgProfileResponse();
        response.organizationId = orgInfo.getId();
        response.email = orgInfo.getEmail();
        response.mobile = orgInfo.getPhone();
        response.orgType = orgInfo.getOrgType().name();
        response.orgname = orgInfo.getOrgname();

        if (profile != null) {

            response.id = profile.getId();
            response.description = profile.getDescription();
            response.legalName = profile.getLegalName();
            response.address1 = profile.getAddress1();
            response.phone =profile.getPhone();
            response.alterMobile =profile.getAlterMobile();
            response.landlineNumber = profile.getLandlineNumber();
            response.address2 = profile.getAddress2();
            response.country = profile.getCountry();
            response.state = profile.getState();
            response.city = profile.getCity();
            response.preferredCurrency = profile.getPreferredCurrency();
            response.locale = profile.getLocale();
            response.taxId = profile.getTaxId();
        }
        System.out.println(response.address1);
        return response;
    }
}

package com.sms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgResponseDTO {
    private String id;
    private String orgname;
    private String orgEmail;
    private String phone;
    private String orgType;
    private String customerSignupUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

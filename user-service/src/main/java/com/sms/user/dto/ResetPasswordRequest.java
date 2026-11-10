package com.sms.user.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String username;
    private String orgId;
    private String newPassword;
}


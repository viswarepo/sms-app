package com.sms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class SendEmailRequest {

    @NotBlank
    @Email
    private String to;

    /** Either supply templateCode + variables, or subject + body directly - not both required. */
    private String templateCode;
    private Map<String, String> variables;

    private String subject;
    private String body;
}

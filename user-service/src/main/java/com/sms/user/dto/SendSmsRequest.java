package com.sms.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class SendSmsRequest {

    /** E.164 format, e.g. +14155552671 */
    @NotBlank
    private String to;

    private String templateCode;
    private Map<String, String> variables;

    private String message;
}

package com.sms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCustomerRequest {
    private String organizationId;
    @NotBlank
    @Email
    private String email;
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String mobile;
    private String customerType;
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

}

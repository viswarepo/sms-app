package com.sms.user.dto;

import com.sms.user.domain.EOrgType;
import com.sms.user.domain.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrgRequestDTO {
    //Org basic parameter
    @NotBlank(message = "Organization name is required")
    private String orgname;
    @NotBlank(message = "Organization email is required")
    @Email
    private String email;
    @NotBlank
    private String mobile;
    private EOrgType orgType;

    //User/customer basic parameter
    @NotBlank
    private String username;
    private String customerType;
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;


}

package com.jackson.modules.app.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class LoginDTO {
    @NotBlank(message = "username不能为Null")
    private String username;
    @NotBlank(message = "password不能为Null")
    private String password;
    //@NotNull(message = "IMEI 不能为NULL")
//    private Long imei;
    private String captcha;
    private String uuid;
}

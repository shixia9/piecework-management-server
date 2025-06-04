package com.jackson.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录表单
 */
@Data
@ApiModel("登录")
public class SysLoginForm {
    @ApiModelProperty(value = "账号", dataType = "string", required = true)
    private String username;
    @ApiModelProperty(value = "密码", dataType = "string", required = true)
    private String password;
    @ApiModelProperty(value = "验证码", dataType = "string", required = true)
    private String captcha;
    @ApiModelProperty(value = "唯一键", dataType = "string", required = true)
    private String uuid;


}

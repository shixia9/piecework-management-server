package com.jackson.modules.app.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.jackson.common.utils.R;
import com.jackson.common.validator.Assert;
import com.jackson.modules.app.dto.LoginDTO;
import com.jackson.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 */
@RestController
@RequestMapping("/app/user")
@Api(tags="app-登录")
@ApiSort(101)
public class LoginController {

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @ApiOperationSupport(order = 1)
    @PostMapping("/login")
    @ApiOperation("登录APP")
    public R login(@Validated @RequestBody LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        Assert.isBlank(username,"账号不能为空");
        Assert.isBlank(password,"密码不能为空");

        R r = userService.login(username, password);

        return r;
    }

}

package com.jackson.modules.app.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.jackson.common.annotation.SysLog;
import com.jackson.common.utils.R;
import com.jackson.common.validator.Assert;
import com.jackson.modules.app.jwt.JwtLogin;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.PasswordForm;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户
 */
@RestController
@RequestMapping("/app/user")
@Api(tags="app-用户")
@ApiSort(102)
public class UserController {

    private SysUserService sysUserService;
    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }


    @Autowired
    EnterpriseService enterpriseService;

    @JwtLogin
    @GetMapping("/info")
    @ApiOperation("获取个人账号信息")
    @ApiOperationSupport(order = 1)
    public R info(@RequestAttribute Long userId) {
        SysUserEntity userEntity = sysUserService.queryByID(userId);

        return R.ok().put("entity", userEntity);
    }

    @JwtLogin
    @GetMapping("/enterprise_info")
    @ApiOperation("获取个人所属企业信息")
    @ApiOperationSupport(order = 2)
    public R enterpriseInfo(@RequestAttribute Long userId) {
        SysUserEntity userEntity = sysUserService.queryByID(userId);
        SysEnterpriseEntity enterpriseEntity = enterpriseService.getById(userEntity.getEnterpriseId());
        return R.ok().put("entity", enterpriseEntity);
    }

    @JwtLogin
    @SysLog("修改密码")
    @PostMapping("/password/update")
    @ApiOperation("更新个人密码")
    @ApiOperationSupport(order = 3)
    public R updatePassword( PasswordForm form, @RequestAttribute Long userId){
        Assert.isBlank(form.getPassword(), "旧密码不为能空");
        Assert.isBlank(form.getNewPassword(), "新密码不为能空");

        //更新密码
        boolean flag = sysUserService.updatePassword(userId, form);
        if(!flag){
            return R.error("原密码不正确");
        }

        return R.ok();
    }


}

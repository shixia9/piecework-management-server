package com.jackson.modules.enterprise.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.jackson.common.annotation.SysLog;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.common.validator.Assert;
import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import com.jackson.modules.enterprise.service.EnterpriseUserService;
import com.jackson.modules.sys.controller.AbstractController;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.entity.SysUserRoleEntity;
import com.jackson.modules.sys.form.SysLoginForm;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enterprise")
@RequiredArgsConstructor
@Api(tags = "企业管理-用户管理")
@ApiSort(100)
public class BussUserController extends AbstractController {

    private final EnterpriseUserService enterpriseUserService;

    private final EnterpriseService enterpriseService;

    private final SysUserService sysUserService;
    @PostMapping("/login")
    @ApiOperation("企业登录")
    public R LoginOn(@RequestBody SysLoginForm form) {
           R r  = enterpriseUserService.LoginOn(form);
           return r ;
    }
    @SysLog("获取当前企业信息")
    @GetMapping("/info")
    @RequiresPermissions("enterprise:info")
    @ApiOperation("获取当前企业信息")
    public R info(){
        return R.ok().put("entity", getEnterpriseEntity ());
    }

    @PostMapping("/worker_info")
    @RequiresPermissions("enterprise:workerInfo")
    @ApiOperation("获取当前企业的员工信息")
    @SysLog("获取当前企业的员工信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "企业id", name = "id", dataType = "string"),
            @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
            @ApiImplicitParam(value = "一页大小", name = Constant.LIMIT, dataType = "string")
    })
    public R workerInfo(@ApiIgnore @RequestParam Map<String, Object> params){
        R r = enterpriseService.queryByEnterpriseID (params);
        return r;
    }
    @SysLog("保存用户")
    @PostMapping("/saveUser")
    @RequiresPermissions("enterprise:user")
    @ApiOperation("保存用户")
    public R save(@RequestBody SysUserEntity user){
        ValidatorUtils.validateEntity(user, AddGroup.class);
        R r = sysUserService.saveUser(user);
        return r;
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @PostMapping("/updateUser")
    @RequiresPermissions("enterprise:user")
    @ApiOperation("更新用户")
    public R update(@RequestBody SysUserEntity user){
        if(user.getId() == Constant.SUPER_ADMIN){
            return R.error("不能更新超级管理信息");
        }
        ValidatorUtils.validateEntity(user, UpdateGroup.class);

        sysUserService.update(user);

        return R.ok();
    }
    @SysLog("删除用户")
    @PostMapping("/deleteUser/{userId}")
    @RequiresPermissions("enterprise:user")
    @ApiOperation("删除用户")
    public R delete(@PathVariable Long userId){
        Assert.isNull(userId,"用户ID不能为空");

        R r = sysUserService.removeUser(userId);
        return r;
    }
}

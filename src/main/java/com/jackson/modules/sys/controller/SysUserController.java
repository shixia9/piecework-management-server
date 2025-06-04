package com.jackson.modules.sys.controller;

import com.jackson.common.annotation.SysLog;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.common.validator.Assert;
import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.PasswordForm;
import com.jackson.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/sys/user")
@Api(tags = "系统管理-用户")
public class SysUserController extends AbstractController {

	private SysUserService sysUserService;
	@Autowired
	public void setSysUserService(SysUserService sysUserService) {
		this.sysUserService = sysUserService;
	}

	@SysLog("获取用户列表")
	@PostMapping("/list")
	@RequiresPermissions("sys:user")
	@ApiOperation("获取用户列表")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "账号", name = "username", dataType = "string"),
			@ApiImplicitParam(value = "姓名", name = "name", dataType = "string"),
			@ApiImplicitParam(value = "角色", name = "roleId", dataType = "string"),
			@ApiImplicitParam(value = "状态，启用：1，停用：0", name = "status", dataType = "string"),
			@ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
			@ApiImplicitParam(value = "一页大小", name = Constant.LIMIT, dataType = "string")
	})
	public R list(@ApiIgnore @RequestParam Map<String, Object> params){
		//只有超级管理员，才能查看所有管理员列表
		if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}

	@GetMapping("/info")
	@RequiresPermissions("sys:user:info")
	@ApiOperation("获取当前登录用户信息")
	public R info(){
		return R.ok().put("user", getUser());
	}

	@SysLog("修改密码")
	@PostMapping("/password")
	@RequiresPermissions("sys:user:updatePassword")
	@ApiOperation("修改当前登录用户密码")
	public R password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getPassword(), "旧密码不为能空");
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");

		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), form);
		if(!flag){
			return R.error("原密码不正确");
		}

		return R.ok();
	}

	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user")
	@ApiOperation("获取指定用户的信息")
	public R info(@PathVariable("userId") Long userId){
		Assert.isNull(userId,"用户ID不能为空");

		if(userId == Constant.SUPER_ADMIN){
			return R.error("不能获取超级管理信息");
		}

		SysUserEntity user = sysUserService.queryByID(userId);

		return R.ok().put("user", user);
	}

	@SysLog("保存用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
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
	@PostMapping("/update")
	@RequiresPermissions("sys:user")
	@ApiOperation("更新用户")
	public R update(@RequestBody SysUserEntity user){
		if(user.getId() == Constant.SUPER_ADMIN){
			return R.error("不能更新超级管理信息");
		}
		ValidatorUtils.validateEntity(user, UpdateGroup.class);

		user.setUpdateUserId(getUserId());
		user.setUpdateTime(new Date());
		sysUserService.update(user);

		return R.ok();
	}

	/**
	 * 删除用户
	 */
	@SysLog("删除用户")
	@PostMapping("/delete/{userId}")
	@RequiresPermissions("sys:user")
	@ApiOperation("删除用户")
	public R delete(@PathVariable Long userId){
		Assert.isNull(userId,"用户ID不能为空");

		R r = sysUserService.removeUser(userId);
		
		return r;
	}
}

package com.jackson.modules.sys.controller;


import com.jackson.common.exception.RRException;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.R;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.SysLoginForm;
import com.jackson.modules.sys.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 登录相关
 */
@Slf4j
@RestController
@Api(tags = "后台登录")
public class SysLoginController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private SysCaptchaService sysCaptchaService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	/**
	 * 验证码
	 */
	@GetMapping("captcha.jpg")
	@ApiOperation("获取登录验证码，返回验证码图片流")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 登录
	 */
	@PostMapping("/sys/login")
	@ApiOperation("登录系统")
	public R login(@RequestBody SysLoginForm form) {

		boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}
		//用户信息
		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());

		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(new Sha256Hash (form.getPassword(), user.getSalt()).toHex())) {
			return R.error("账号或密码不正确");
		}
		//用户拥有角色
		List<Long> longs = sysUserRoleService.queryRoleIdList(user.getId());

		//账号锁定
		if (enterpriseService.getById(user.getEnterpriseId()).getStatus() == 0){
			return R.error("企业服务已停用");

		}else if(user.getStatus() == 0){
			return R.error("账号已被锁定,请联系管理员");

		}else if(longs.contains(Constant.RoleID.WORKER_STEEL.getValue())
				|| longs.contains(Constant.RoleID.WORKER_BARCODE.getValue())
					||longs.contains(Constant.RoleID.ENTERPRISE_ADMIN.getValue())){
			return R.error("App用户与企业管理人员不能登录此系统");
		}
		//生成token，并保存到数据库
		R r = sysUserTokenService.createToken(user.getId());

		return r.put("msg","登录成功");
	}


	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	@ApiOperation("登出系统")
	public R logout() {
		sysUserTokenService.logout(getUserId());
		return R.ok();
	}
	
}

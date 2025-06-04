package com.jackson.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.PasswordForm;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 根据ID，查询系统用户（不包含密码等信息）
	 */
	SysUserEntity queryByID(Long userId);

	/**
	 * 保存用户
	 */
	R saveUser(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);

	/**
	 * 修改密码
	 * @param form
	 */
	boolean updatePassword(Long userId, PasswordForm form);

	/**
	 * 删除用户
	 * @param userId
	 */
    R removeUser(Long userId);
}

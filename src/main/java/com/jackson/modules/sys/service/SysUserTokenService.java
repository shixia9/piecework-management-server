package com.jackson.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.R;
import com.jackson.modules.sys.entity.SysUserTokenEntity;

/**
 * 用户Token
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	R createToken(long userId);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(long userId);

	/**
	 * 获取token对象
	 * @param token
	 * @return
	 */
	SysUserTokenEntity queryByToken(String token);

}

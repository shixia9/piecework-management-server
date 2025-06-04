package com.jackson.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;

import com.jackson.modules.sys.service.EnterpriseService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controller公共组件
 */
public abstract class AbstractController {
	@Autowired
	EnterpriseService enterpriseService;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUserEntity getUser() {
		return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
	}

	protected Long getUserId() {
		return getUser().getId();
	}

	protected SysEnterpriseEntity getEnterpriseEntity() {
		return enterpriseService.getById(getUser().getEnterpriseId());
	}
}

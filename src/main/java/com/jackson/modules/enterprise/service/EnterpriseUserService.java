package com.jackson.modules.enterprise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.SysLoginForm;
import com.jackson.common.utils.R;


public interface EnterpriseUserService extends IService<SysUserEntity> {


    R LoginOn(SysLoginForm sysLoginForm);


}

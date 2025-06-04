package com.jackson.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

public interface EnterpriseService extends IService<SysEnterpriseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R queryByEnterpriseID( Map<String, Object> params);

    void updateEnterprise(SysEnterpriseEntity enterprise, Long userId);

    void saveEnterprise(SysEnterpriseEntity enterprise);
}


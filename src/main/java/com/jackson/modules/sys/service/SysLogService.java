package com.jackson.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.PageUtils;
import com.jackson.modules.sys.entity.SysLogEntity;

import java.util.Map;

/**
 * 系统日志
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

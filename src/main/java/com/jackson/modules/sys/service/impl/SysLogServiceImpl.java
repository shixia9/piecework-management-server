/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.jackson.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.Query;
import com.jackson.modules.sys.dao.SysLogDao;
import com.jackson.modules.sys.entity.SysLogEntity;
import com.jackson.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String operation = (String)params.get("operation");
        String username = (String)params.get("name");
        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate");

        String text = (String)params.get("text");


        IPage<SysLogEntity> page = this.page(
            new Query<SysLogEntity>().getPage(params,"create_date",false),
            new QueryWrapper<SysLogEntity>().like(StringUtils.isNotBlank(operation),"operation", operation)
                .like(StringUtils.isNotBlank(username),"username", username)
                .ge (StringUtils.isNotBlank(startDate),"create_date", startDate)
                .le (StringUtils.isNotBlank(endDate),"create_date",endDate)
        );

        return new PageUtils(page);
    }
}

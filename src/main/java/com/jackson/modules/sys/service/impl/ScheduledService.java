package com.jackson.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jackson.common.utils.Constant;
import com.jackson.modules.sys.controller.AbstractController;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Async("myExecutor")
public class  ScheduledService extends AbstractController {

    @Autowired
    private EnterpriseService enterpriseService;
    @Scheduled(cron = "0 0 0 * * ? ")
    public void hello (){
        final List<SysEnterpriseEntity> list = enterpriseService.list ();
        for (SysEnterpriseEntity entity: list
             ) {
            if (entity.getRentEndTime ().getTime () < new Date ().getTime ()){
                enterpriseService.update (new UpdateWrapper<SysEnterpriseEntity> ()
                        .eq ("id",entity.getId ())
                        .set ("status", Constant.UserStatus.OFF.getValue()));
            }
        }
        logger.info("task1 execute");
    }
}

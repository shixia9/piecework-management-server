package com.jackson.modules.enterprise.service.imp;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.R;
import com.jackson.modules.enterprise.service.EnterpriseUserService;
import com.jackson.modules.sys.dao.SysUserDao;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.SysLoginForm;
import com.jackson.modules.sys.service.*;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;



@Service("sysBussUseService")
public class SysBussUserServiceImp extends ServiceImpl<SysUserDao,SysUserEntity > implements EnterpriseUserService {

   @Autowired
   private SysUserService sysUserService;
   @Autowired
   private SysUserRoleService sysUserRoleService;
   @Autowired
   private EnterpriseService enterpriseService;
   @Autowired
   private SysUserTokenService sysUserTokenService;
   @Autowired
   private SysCaptchaService sysCaptchaService;

    @Override
    public R LoginOn(SysLoginForm form) {

        boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
        if(!captcha){
            return R.error("验证码不正确");
        }

        //用户信息
        SysUserEntity user = sysUserService.queryByUserName(form.getUsername());
        //用户拥有角色
        List<Long> longs = sysUserRoleService.queryRoleIdList(user.getId());

        //账号不存在、密码错误
        if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
            return R.error("账号或密码不正确");
        }
        //账号锁定
        if (enterpriseService.getById(user.getEnterpriseId()).getStatus() == 0){
            return R.error("企业服务已停用");

        }else if(user.getStatus() == 0){
            return R.error("账号已被锁定,请联系管理员");

        }else if(longs.contains(Constant.RoleID.WORKER_STEEL.getValue())
                || longs.contains(Constant.RoleID.WORKER_BARCODE.getValue())){
            return R.error("App用户不能登录此系统");
        }
        R r = sysUserTokenService.createToken(user.getId());
        user.setLogoutTime(new Date());
        sysUserService.updateById(user);
        return r.put("msg","登录成功").put("enterpriseId",user.getEnterpriseId());
    }

}

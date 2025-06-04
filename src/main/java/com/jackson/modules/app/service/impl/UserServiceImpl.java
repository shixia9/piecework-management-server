package com.jackson.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackson.common.exception.RRException;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.R;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.jwt.JwtUtils;
import com.jackson.modules.app.mapper.ImageMapper;
import com.jackson.modules.app.service.UserService;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserRoleService;
import com.jackson.modules.sys.service.SysUserService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private SysUserService sysUserService;

    private JwtUtils jwtUtils;
    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }


    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Override
    public R login(String username, String password) {
            String token;
            //验证账号密码
            SysUserEntity user = sysUserService.queryByUserName(username);


            //账号不存在、密码错误
            if(user == null || !user.getPassword().equals(new Sha256Hash (password, user.getSalt()).toHex())) {
                throw new RRException("账号或密码不正确");
            }
            List<Long> longs = sysUserRoleService.queryRoleIdList(user.getId());
            //账号锁定
            if (enterpriseService.getById(user.getEnterpriseId()).getStatus() == 0){
                return R.error("企业服务已停用");

            }else if(user.getStatus() == 0){
                return R.error("账号已被锁定,请联系管理员");

            }else if(
                    !longs.contains(Constant.SUPER_ADMIN)             //只有超管跟worker可以登录
                    && !longs.contains(Constant.RoleID.WORKER_STEEL.getValue())
                    && !longs.contains(Constant.RoleID.WORKER_BARCODE.getValue())){
                return R.error("PC用户不能登录此系统");
            }
            token = jwtUtils.generateToken(user.getId());
            user.setLogoutTime(new Date());
            sysUserService.updateById(user);

        return R.ok().put("token",token).put("id",user.getId());
    }

}

package com.jackson.modules.sys.service.impl;

import com.jackson.common.utils.Constant;
import com.jackson.modules.sys.entity.SysMenuEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.entity.SysUserTokenEntity;
import com.jackson.modules.sys.service.ShiroService;
import com.jackson.modules.sys.service.SysMenuService;
import com.jackson.modules.sys.service.SysUserService;
import com.jackson.modules.sys.service.SysUserTokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuService sysMenuDao;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserTokenService sysUserTokenService;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            List<SysMenuEntity> menuList = sysMenuDao.getUserMenuList(Constant.SUPER_ADMIN);
            permsList = new ArrayList<>(menuList.size());
            for (SysMenuEntity menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = sysUserService.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {

        return sysUserTokenService.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {

        return sysUserService.queryByID(userId);

    }
}

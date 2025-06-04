package com.jackson.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.exception.RRException;
import com.jackson.common.utils.*;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.sys.dao.SysUserDao;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.form.PasswordForm;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysRoleService;
import com.jackson.modules.sys.service.SysUserRoleService;
import com.jackson.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.jackson.common.utils.ShiroUtils.*;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysUserService")

public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    private SysUserRoleService sysUserRoleService;
    private SysRoleService sysRoleService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    @Lazy
    private AIService aiService;
    @Autowired
    @Lazy
    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }
    @Autowired
    public void setSysUserRoleService(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }
    private enum OperateType {SAVE,UPDATE}

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        String name = (String) params.get("name");
        String status = (String) params.get("status");
        String roleId = (String) params.get("roleId");


        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), "username", username)
                .like(StringUtils.isNotBlank(name), "name", name)
                .eq(StringUtils.isNotBlank(status), "status", status)
                .orderByDesc("create_time")
                .select("id", "username", "name","phone","create_user_id", "create_time", "status");

        //IPage<Map<String, Object>> page = this.pageMaps(new Query<SysUserEntity>().getPageMap(params), wrapper);
        //上下两种写法相同
        List<SysUserEntity> list = this.list (wrapper);

        for (SysUserEntity user: list
        ) {
            List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getId());
            //插入创建人姓名以及角色信息
            user.setCreateUserName ( this.getById (user.getCreateUserId ()).getName ());
            user.setRoleIdList(roleIdList);

        }
        Predicate<SysUserEntity> admin = x -> x.getRoleIdList().contains(Constant.RoleID.ENTERPRISE_ADMIN.getValue()) || x.getRoleIdList().contains(Constant.RoleID.ADMIN.getValue());
        List<SysUserEntity> adminList = list.stream().filter(admin).collect(Collectors.toList());

        List<SysUserEntity> finalList;

        //手写分页 设置分页参数
        int curPage = 1;
        int limit = 10;

        if(params.get(Constant.PAGE) != null){
            String pageParam = (String)params.get(Constant.PAGE);
            if(StringUtils.isNotBlank(pageParam)) {
                curPage = Integer.parseInt (pageParam);
            }
        }
        if(params.get(Constant.LIMIT) != null){
            String limitParam = (String)params.get(Constant.LIMIT);
            if(StringUtils.isNotBlank(limitParam)) {
                limit = Integer.parseInt (limitParam);
            }
        }
        if (StringUtils.isNotBlank(roleId)){
            //筛选符合输入的角色条件的用户
            Predicate<SysUserEntity> contian = x -> x.getRoleIdList().contains(Long.parseLong(roleId));
            finalList = adminList.stream().filter(contian).collect(Collectors.toList());
        }else {
            finalList = adminList;
        }

        IPage page = new Query<> ().listToPage (finalList,curPage,limit);
        return new PageUtils(page);
    }



    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    public SysUserEntity queryByID(Long userId) {
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId)
                .select("id", "name","username","create_user_id", "enterprise_id", "phone",  "status");

        SysUserEntity userEntity = this.getOne(wrapper);
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userEntity.getId());
        userEntity.setRoleIdList(roleIdList);

        userEntity.setCreateUserName ( this.getById (userEntity.getCreateUserId ()).getName ());
        return userEntity;
    }

    @Override
    @Transactional
    public R saveUser(SysUserEntity user) {
        //检查账号是否唯一
        checkUsername(user, OperateType.SAVE);
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setCreateUserId(getUserId());
        user.setUpdateUserId (getUserId());
        //查看当前登录用户角色
        List<Long> roleId = getUserEntity ().getRoleIdList ();
        //TODO 超级管理员与

        if (roleId.contains (Constant.RoleID.ADMIN.getValue ()) || roleId.contains (Constant.RoleID.SUPERADMIN.getValue ())){

            if (user.getEnterpriseId() == null){//说明创建的是系统管理员
                user.setEnterpriseId(getUserEntity ().getEnterpriseId ());
            }
        }
        this.save(user);
        //检查角色是否越权
        //checkRole(user);


        boolean contains = roleId.contains (Constant.RoleID.ENTERPRISE_ADMIN.getValue ());
        //保存用户与角色关系
        if (contains){
            List<Long> roleIdList = new ArrayList<> ();
            //若是企业管理员新增用户则角色,enterpriseId 根据公司的function与ID 自动赋值
            SysEnterpriseEntity enterprise = enterpriseService.getById (getUserEntity ().getEnterpriseId ());
            //上面账号已经添加
            if (enterprise.getAccountUsed() + 1 >enterprise.getAccountLimit()){
                //暂时手动把上面添加用户的删除
                this.removeById(user.getId());
                return R.error("当前公司app账号额度已满,请联系管理员");
            }
            //修正已使用账号的数量

            enterprise.setAccountUsed(enterprise.getAccountUsed() + 1);
            enterprise.setUpdaterId(getUserId());
            enterprise.setUpdateTime(new Date());
            enterpriseService.updateById(enterprise);
            //判断可用算法
            String[] split = enterprise.getFunction ().split (",");
            for (String s:split
            ) {
                if (s.equals (Constant.AlgorithmType.STEEL.getValue ())){
                    roleIdList.add (Constant.RoleID.WORKER_STEEL.getValue ());
                }else if (s.equals (Constant.AlgorithmType.BARCODE.getValue ())){
                    roleIdList.add (Constant.RoleID.WORKER_BARCODE.getValue ());
                }
            }
            sysUserRoleService.saveOrUpdate(user.getId(), roleIdList);
            this.update (new UpdateWrapper<SysUserEntity> ()
                    .eq ("username",user.getUsername ())
                    .set ("enterprise_id",getUserEntity ().getEnterpriseId ()));
        }else {
            //若是系统管理员新增用户则角色手动赋值
            sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());
        }

        return R.ok();
    }

    @Override
    @Transactional
    public void update(SysUserEntity user) {
        //检查账号是否唯一
        checkUsername(user, OperateType.UPDATE);


        //默认只能添加管理员账号
        //        List<Long> roleIdList = new ArrayList<>();
        //        roleIdList.add(Constant.RoleID.ADMIN.getValue());
        //        user.setRoleIdList(roleIdList);

        //更新人及更新时间
        user.setUpdateUserId(getUserId ());
        user.setUpdateTime(new Date());

        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            String salt = RandomStringUtils.randomAlphanumeric(20);
            user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
            user.setSalt(salt);
        }
        if (user.getEnterpriseId () == null){
            user.setEnterpriseId(null);
        }
        this.updateById(user);

        //检查角色是否越权
        //checkRole(user);

        //企业管理修改用户信息
        if (user.getRoleIdList() != null) {
            //保存用户与角色关系
            if (user.getRoleIdList().size() != 0){
                sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());
            }
        }
    }

    @Override
    public boolean updatePassword(Long userId, PasswordForm form) {
        SysUserEntity userEntity = this.getById(userId);
        //sha256加密
        String password = new Sha256Hash(form.getPassword(), userEntity.getSalt()).toHex();
        //旧密码判断
        if(!password.equals(userEntity.getPassword())){
            return false;
        }
        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(), userEntity.getSalt()).toHex();
        userEntity.setPassword(newPassword);

        return this.updateById(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public R removeUser(Long userId) {
        if(userId ==  Constant.SUPER_ADMIN){
            return R.error("超级管理员管理员不能删除");
        }
        if(userId == getUserId()){
            return R.error("当前用户不能删除");
        }
        SysEnterpriseEntity enterprise = enterpriseService.getById(getUserEntity().getEnterpriseId());

        //若当前用户为企业管理员 则需要修改相应 公司账号额度 用户角色表
        if (!getUserEntity().getRoleIdList().contains(Constant.RoleID.SUPERADMIN.getValue())){
            //当前公司名下账号范围
            List<SysUserEntity> list = this.list(new QueryWrapper<SysUserEntity>().eq("enterprise_id", enterprise.getId()));
            for (SysUserEntity user:list
            ) {
                if (userId.equals(user.getId())){
                        this.removeById(userId);
                        aiService.removeByMap(new MapUtils().put("user_id", userId));
                        //公司账号额度
                        enterprise.setAccountUsed(enterprise.getAccountUsed() - 1);
                        enterprise.setUpdaterId(getUserId());
                        enterprise.setUpdateTime(new Date());
                        enterpriseService.updateById(enterprise);
                        //角色
                        sysUserRoleService.removeByMap(new MapUtils().put("user_id", userId));
                        return R.ok("删除成功");
                }
            }
            return R.error("只能删除贵公司名下的用户");
        }
        //当前用户为超级管理员或者系统管理员
        this.removeById(userId);
        aiService.removeByMap(new MapUtils().put("user_id", userId));
        sysUserRoleService.removeByMap(new MapUtils().put("user_id", userId));

        return  R.ok();
    }

    /**
     * 检查角色是否越权
     */
    private void checkRole(SysUserEntity user) {
        if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
            return;
        }
        //如果不是超级管理员，则需要判断用户的角色是否自己创建
        if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
            return;
        }

        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

        //判断是否越权
        if (!roleIdList.containsAll(user.getRoleIdList())) {
            throw new RRException("新增用户所选角色，不是本人创建");
        }
    }

    /**
     * 检查账号是否唯一
     * @param user 用户
     * @param type 1:保存，2:更新
     */
    private void checkUsername(SysUserEntity user,OperateType type){
        if(OperateType.SAVE == type){
            SysUserEntity sysUserEntity = this.getOne(new QueryWrapper<SysUserEntity>().eq("username", user.getUsername()));
            if(sysUserEntity!=null){
                throw new RRException("系统中已存在账号【"+user.getUsername()+"】");
            }
        }else{
            QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("username",user.getUsername());
            wrapper.ne("id",user.getId());
            SysUserEntity sysUserEntity = this.getOne(wrapper);
            if(sysUserEntity!=null){
                throw new RRException("系统中已存在账号【"+user.getUsername()+"】");
            }
        }
    }

}
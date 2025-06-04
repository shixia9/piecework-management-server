package com.jackson.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.exception.RRException;
import com.jackson.common.utils.*;
import com.jackson.common.validator.Assert;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.sys.dao.SysEnterpriseDao;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.entity.SysUserTokenEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserRoleService;
import com.jackson.modules.sys.service.SysUserService;
import com.jackson.modules.sys.service.SysUserTokenService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.jackson.common.utils.ShiroUtils.getUserId;
import static java.lang.Integer.parseInt;


@Service("enterpriseService")
public class EnterpriseServiceImpl extends ServiceImpl<SysEnterpriseDao, SysEnterpriseEntity> implements EnterpriseService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    private enum OperateType {SAVE,UPDATE}


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        //多条件查询
        QueryWrapper wrapper = new QueryWrapper<SysEnterpriseEntity>();
        if (StringUtils.isNotBlank ((String)params.get ("name"))){
            wrapper.like ("name",params.get ("name"));
        }
        if (StringUtils.isNotBlank ((String)params.get ("rentEndTime"))){
            //从现在开始到指定日期
            wrapper.ge ("rent_end_time", new Date ());
            wrapper.le ("rent_end_time",params.get ("rentEndTime"));
        }
        if (StringUtils.isNotBlank ((String)params.get ("status"))){
            wrapper.eq ("status",params.get ("status"));
        }
        IPage<SysEnterpriseEntity> page = this.page(
                new Query<SysEnterpriseEntity>().getPage(params,"rent_end_time",true),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public R queryByEnterpriseID(Map<String, Object> params) {
        List<SysUserEntity> sysUserEntities = sysUserService.list (
                new QueryWrapper<SysUserEntity> ()
                        .eq (StringUtils.isBlank((String) params.get("id")),"enterprise_id", ShiroUtils.getUserEntity().getEnterpriseId())
                        .eq (StringUtils.isNotBlank((String) params.get("id")),"enterprise_id", params.get("id"))
                        .select("id", "username", "name","phone","logout_time","create_user_id", "create_time", "status")
        );
        //修改数据以显示 创建人 以及 最后登录时间
        for (SysUserEntity user: sysUserEntities
        ) {
            List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getId());
            //插入创建人姓名以及角色信息
            user.setRoleIdList(roleIdList);
            user.setCreateUserName (sysUserService.queryByID (user.getCreateUserId ()).getName ());
            //防止有账号后未登录没有token 从而引发空指针异常
            SysUserTokenEntity token = sysUserTokenService.getById (user.getId ());
            if (token != null){
                user.setLogoutTime (token.getUpdateTime ());
            }
        }
        //在sysUserEntities里面筛选出App用户
        Predicate<SysUserEntity> app = x -> x.getRoleIdList().contains(Constant.RoleID.WORKER_STEEL.getValue())||x.getRoleIdList().contains(Constant.RoleID.WORKER_BARCODE.getValue());

        List<SysUserEntity> appList = sysUserEntities.stream().filter(app).collect(Collectors.toList());
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

        IPage page = new Query<> ().listToPage (appList,curPage,limit);

        return R.ok().put("page", new PageUtils(page)).put("list",appList);
    }

    @Override
    public void updateEnterprise(SysEnterpriseEntity enterprise,Long updaterId) {
        enterprise.setUpdateTime (new Date ());
        enterprise.setUpdaterId (updaterId );
        //判断可用算法
        String[] split = enterprise.getFunction ().split (",");
        for (String s:split
        ) {
            if (!s.equals (Constant.AlgorithmType.STEEL.getValue ()) && !s.equals (Constant.AlgorithmType.BARCODE.getValue ())){
                throw new RRException ("请输入指定算法字符串");
            }
        }
        this.updateById (enterprise);
    }

    @Override
    public void saveEnterprise(SysEnterpriseEntity enterprise) {
        //检查公司名字是否唯一
        checkEnterpriseName(enterprise,EnterpriseServiceImpl.OperateType.SAVE);
        enterprise.setRentStartTime(new Date());
        enterprise.setRentEndTime(DateUtils.addDateDays(new Date(),1));

        enterprise.setAccountUsed(0);
        enterprise.setAccountLimit(0);
        enterprise.setStorageLimit (0f);
        enterprise.setStorageUsed (0f);
        enterprise.setCreateTime (new Date ());
        enterprise.setUpdateTime(new Date());
        enterprise.setUpdaterId(getUserId ());
        enterprise.setCreateUserId (getUserId ());
        this.save(enterprise);
    }
    /**
     * 检查账号是否唯一
     * @param enterprise 公司
     * @param type 1:保存，2:更新
     */
    private void checkEnterpriseName(SysEnterpriseEntity enterprise, EnterpriseServiceImpl.OperateType type){
        if(EnterpriseServiceImpl.OperateType.SAVE == type){
            SysEnterpriseEntity enterpriseEntity = this.getOne(new QueryWrapper<SysEnterpriseEntity>().eq("`name`", enterprise.getName()));
            if(enterpriseEntity!=null){
                throw new RRException("系统中已存在名为【"+enterprise.getName()+"】的公司");
            }
        }else{
            QueryWrapper<SysEnterpriseEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("`name`",enterprise.getName());
            wrapper.ne("id",enterprise.getId());
            SysEnterpriseEntity enterpriseEntity = this.getOne(wrapper);
            if(enterpriseEntity!=null){
                throw new RRException("系统中已存在名为【"+enterprise.getName()+"】的公司");
            }
        }
    }
}
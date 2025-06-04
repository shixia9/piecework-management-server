package com.jackson.modules.enterprise.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.utils.*;

import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.dao.SteelTypeMapper;
import com.jackson.modules.enterprise.service.SteelTypeService;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;



@Service("steelTypeService")
public class SteelTypeServiceImpl extends ServiceImpl<SteelTypeMapper, SteelTypeEntity>implements SteelTypeService {


    @Autowired
    private SysUserService userService;
    @Autowired
    @Lazy
    private AIService aiService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SteelTypeEntity> page = this.page(
                new Query<SteelTypeEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils showSteelType(SysUserEntity user,Map<String, Object> params) {
        IPage<SteelTypeEntity> page = this.page(
                new Query<SteelTypeEntity>().getPage(params),
                new QueryWrapper<SteelTypeEntity>()
                        .eq("enterprise_id",user.getEnterpriseId())
        );

        page.getRecords ().stream ().forEach (x->x.setUsername (userService.getById(user.getId()).getName()));
        return new PageUtils(page);

    }

    @Override
    public R  updateSteelType(SteelTypeEntity steelType,SysUserEntity user) {
//        QueryWrapper<SteelTypeEntity> wrapper = new QueryWrapper<>();
//        wrapper.eq("steel_id",steelType.getSteelId());
//        SteelTypeEntity OldSteelType = baseMapper.selectOne(wrapper);
        SteelTypeEntity OldSteelType = this.getById(steelType.getSteelId());
        if (steelType.getBaseNum() != null){
            List<ImageEntity> image = aiService.listByMap(new MapUtils().put("steel_id", steelType.getSteelId()));
            image.stream().forEach(x->x.setNum(x.getNum()/OldSteelType.getBaseNum() * (steelType.getBaseNum())));
            aiService.updateBatchById(image);
        }
        OldSteelType.setStatus(steelType.getStatus());
        OldSteelType.setBaseNum(steelType.getBaseNum());
        OldSteelType.setName(steelType.getName());
        OldSteelType.setUpdatorId(user.getId());
        OldSteelType.setUpdateTime(new Date());
        if (OldSteelType.getName() == null && OldSteelType.getBaseNum() == 0) {
            return R.error("请输入正确的工件号或者数量");
        }
        System.out.println(OldSteelType);
        baseMapper.updateById(OldSteelType);
        return R.ok();
    }

    @Override
    public R saveNewSteelType(SteelTypeEntity steelType,SysUserEntity user) {
        ValidatorUtils.validateEntity(steelType, AddGroup.class);
        if (steelType.getName() == null && steelType.getBaseNum() <= 0) {
            return R.error("请输入正确的工件号或者数量");
        }
        steelType.setCreateUserId(user.getId());
        steelType.setEnterpriseId(user.getEnterpriseId());
        steelType.setCreateTime(new Date());
        steelType.setUpdateTime(new Date());
        this.save(steelType);
        return R.ok();
    }

    @Override
    public R steelTypeList(Long steelId,Long userId) {
        SysUserEntity userEntity = userService.queryByID(userId);
        QueryWrapper<SteelTypeEntity> wrapper = new QueryWrapper<SteelTypeEntity>()
                .eq("status", 1)
                .eq("enterprise_id",userEntity.getEnterpriseId())
                .eq (steelId != null,"steel_id",steelId);
        return R.ok().put("list",this.list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void updateBaseNum(SteelTypeEntity steelType, Long userId) {
        Integer oldBaseNum = this.getById(steelType.getSteelId()).getBaseNum();
        this.update(new UpdateWrapper<SteelTypeEntity>()
                .set("base_num",steelType.getBaseNum())
                .eq("steel_id",steelType.getSteelId()));
        List<ImageEntity> image = aiService.listByMap(new MapUtils().put("steel_id", steelType.getSteelId()));
        image.stream().forEach(x->x.setNum(x.getNum()/oldBaseNum * (steelType.getBaseNum())));
        aiService.updateBatchById(image);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void delete(List<Long> asList) {
        this.removeByIds(asList);
        aiService.remove(new QueryWrapper<ImageEntity>().lambda().in(ImageEntity::getSteelId,asList));
    }


}
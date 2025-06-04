package com.jackson.modules.enterprise.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.vo.SteelTypeVO;
import com.jackson.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

public interface SteelTypeService extends IService<SteelTypeEntity>{

    PageUtils queryPage(Map<String, Object> params);

    PageUtils showSteelType(SysUserEntity user,Map<String, Object> params);

    R updateSteelType(SteelTypeEntity steelType,SysUserEntity user);

    R saveNewSteelType(SteelTypeEntity steelType,SysUserEntity user);

    R steelTypeList(Long steelId,Long userId);

    void updateBaseNum(SteelTypeEntity steelType, Long userId);

    void delete(List<Long> asList);
}


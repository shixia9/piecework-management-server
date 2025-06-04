package com.jackson.modules.enterprise.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.jackson.common.utils.PageUtils;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.enterprise.entity.BussImageEntity;
import com.jackson.modules.enterprise.vo.ImageUserVO;
import com.jackson.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

public interface BussImageService extends IService<BussImageEntity> {

    List<ImageUserVO> steelTotal(SysUserEntity user, Map<String,Object> params);

    PageUtils imageDetail(Map<String, Object> params);
}

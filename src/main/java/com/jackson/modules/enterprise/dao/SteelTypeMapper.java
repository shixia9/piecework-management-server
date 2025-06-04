package com.jackson.modules.enterprise.dao;

import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SteelTypeMapper extends BaseMapper<SteelTypeEntity> {

	List<SteelTypeEntity> selectSteelTypeDetails(Long enterpriseId);


}

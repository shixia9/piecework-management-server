package com.jackson.modules.app.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jackson.modules.app.entity.ImageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper extends BaseMapper<ImageEntity> {
}

package com.jackson.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("image")
public class ImageEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String url;
    private String labelString;
    private Long userId;
    private Date createTime;
    private Date updateTime;
    private Integer width;
    private Integer height;
    @TableField("`name`")
    private String name;
    @TableField("`check`")
    private String check; //隶属于哪个功能。
    @TableField(exist = false)
    private String steelTypeName;
    private Long steelId;
    @ApiModelProperty(value = "标注框JsonStringList信息")
    @TableField(exist = false)
    private List labelStringList;
    private Integer num  = 0 ;
    private String manualNumList;
    @TableField(exist = false)
    private String absName;
    @JsonIgnore
    private Long enterpriseId;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")

}

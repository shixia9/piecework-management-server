package com.jackson.modules.enterprise.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "用户操作记录")
public class ImageUserVO {
    @ApiModelProperty(value = "图片ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @ApiModelProperty(value = "账号")
    private String createUserName;
    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "时间字符串")
    private String createTimeStr;
    @ApiModelProperty(value = "工件号")
    private String steelTypeName;
    @ApiModelProperty(value = "工件号ID")
    private Long steelId;
    @ApiModelProperty(value = "图片数量")
    private Integer imageNum;
    @ApiModelProperty(value = "计数")
    private Integer num;
}

package com.jackson.modules.enterprise.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;

@Data
@TableName("image")
public class BussImageEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Date createTime;
    @TableField(exist = false)
    private String createTimeStr;
    @TableField(exist = false)
    private String steelTypeName;
    private Long steelId;
    private Integer num;
    @TableField(exist = false)
    private String createUserName;
    @TableField("`name`")
    private String name;

}
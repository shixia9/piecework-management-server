package com.jackson.modules.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description="")
public class HistoryDTO {

    @NotEmpty(message = "startTime不允许为空")
    @ApiModelProperty(value = "示例:1970-01-01 12:12:12")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime; //历史的某个时间点
    @NotEmpty(message = "endTime不允许为空")
    @ApiModelProperty(value = "示例:2021-07-17 12:12:12")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String steelType;

    private Long steelId;
}

package com.jackson.modules.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@ApiModel(description="")
@Accessors(chain = true)
public class ImageVO {
    @ApiModelProperty(value = "图片ID")
    private Long id;
    @ApiModelProperty(value = "后端保存的图片名")
    private String name;
    @ApiModelProperty(value = "标注框JsonStringList信息")
    private List labelString;
//    @ApiModelProperty(value = "文件原来的名字")
//    private String absName;
    @ApiModelProperty(value = "图片创建时间")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "钢材号")
    private Integer type;
    @ApiModelProperty(value = "标注框数量")
    private Integer num;
}

package com.jackson.modules.enterprise.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标注框
 */
@Data
@ApiModel("标注框信息")
public class LabelBoxForm {

    @ApiModelProperty(value = "标注框名称", dataType = "string", required = true)
    private String name;

    @ApiModelProperty(value = "右-全景图ID", dataType = "long", required = true)
    private Long rightPanoramaId;

    @ApiModelProperty(value = "右-标注信息", dataType = "string", required = true)
    private String rightMessage;

    @ApiModelProperty(value = "左-全景图ID", dataType = "long")
    private Long leftPanoramaId;

    @ApiModelProperty(value = "左-标注信息", dataType = "string")
    private String leftMessage;

    @ApiModelProperty(value = "同步生成报告", dataType = "string", notes = "0：不生成报告，1：生成报告")
    private String genReport;
}

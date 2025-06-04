package com.jackson.modules.app.dto;

import com.jackson.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
public class ImageDTO {
    @NotNull(message="Id不能为NULL",groups = {UpdateGroup.class})
    private Long id;
    @NotBlank(message = "labelString不允许为空",groups = {UpdateGroup.class})
    private String labelString;

    private Date createTime;

    private String steelTypeName;

    private List list;

    private Long steelId;
}

class type {
    String name;
    Integer num;
}

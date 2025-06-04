package com.jackson.modules.enterprise.vo;

import lombok.Data;

import java.util.Date;
import java.util.function.LongFunction;

@Data
public class SteelTypeVO {

    private Long steeId;
    /**
     * 钢材类型
     */
    private String name;
    /**
     *
     */
    private Integer baseNum;
    /**
     *
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String username;
}

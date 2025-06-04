package com.jackson.common.utils;

import lombok.Data;

/**
 * 填充PDF对象类
 */
@Data
public class PDFFillForm {
    /**
     * 填充名称
     */
    private String name;
    /**
     * 填充类型，1：文字，2：图片
     */
    private String type;

    /**
     * 填充内容，文字：值，图片：路径
     */
    private String content;
}

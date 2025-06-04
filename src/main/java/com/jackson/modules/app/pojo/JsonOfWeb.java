package com.jackson.modules.app.pojo;


import lombok.Data;

import java.util.Arrays;

/**
 * 将JsonOfAI解析成这种我们需要的格式
 */
@Data
public class JsonOfWeb {
    private String imgName; //图片名
    private Integer[] point;//框的位置
    private String stroke; //颜色
    private Double rotation;//角度
    private String barcode ;//识别出的条形码

    public JsonOfWeb() {
    }
    @Override
    public String toString() {
        return "JsonOfWeb{" +
                "imgName='" + imgName + '\'' +
                ", point=" + Arrays.toString(point) +
                ", stroke='" + stroke + '\'' +
                ", rotation=" + rotation +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
package com.jackson.modules.app.vo;

/**
 * 前端的Json文件对象
 */

import java.util.Arrays;

public class JsonOfWeb {
    private String imgName; //图片名
    private Integer[] point;//框的位置
    private String stroke; //颜色
    private Double rotation;//角度
    private String barcode ;//识别出的条形码

    public JsonOfWeb() {
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Integer[] getPoint() {
        return point;
    }

    public void setPoint(Integer[] point) {
        this.point = point;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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
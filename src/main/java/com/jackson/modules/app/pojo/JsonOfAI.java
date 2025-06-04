package com.jackson.modules.app.pojo;

import lombok.Data;

/**
 * 算法给的json文件格式
 */
@Data
public class JsonOfAI {
    private String imageName;
    private String categoryName;
    private String coordinateInfo;
    private String rotateCoordinateInfo;
    private String angle;
}
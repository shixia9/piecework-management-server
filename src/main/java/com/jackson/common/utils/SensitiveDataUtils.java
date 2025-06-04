package com.jackson.common.utils;

/**
 * 敏感数据处理
 */
public class SensitiveDataUtils {

    public static String phoneShield(String phone){
       return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    }

    public static String idCardShield(String idCard){
        return idCard.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1****$2");
    }

}

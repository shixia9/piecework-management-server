package com.jackson.common.validator;

import com.jackson.common.exception.RRException;
import org.apache.commons.lang.StringUtils;

/**
 * 数据校验
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new RRException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new RRException(message);
        }
    }

    public static void isNotNull(Object object, String message) {
        if (object != null) {
            throw new RRException(message);
        }
    }
    public static void isTheOne(Object object, Integer num,String message) {
        if (object == null || object == num) {
            throw new RRException(message);
        }
    }
}

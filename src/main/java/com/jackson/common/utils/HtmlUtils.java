package com.jackson.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html工具类
 */
public class HtmlUtils {

    /**
     * 去掉html内容中的所有html元素
     * @param content
     * @return
     */
    public static String dealMark(String content) {
        String regx = "(<.+?>)|(</.+?>)";
        Matcher matcher = Pattern.compile(regx).matcher(content);
        while (matcher.find()) {
            content = matcher.replaceAll("").replace(" ", "");
        }

        return content;
    }

}

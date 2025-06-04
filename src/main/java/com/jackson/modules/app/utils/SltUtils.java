package com.jackson.modules.app.utils;
public class SltUtils {
    /**
     * 输入 "d:/file/WJ/affa0994987549d19c5c3f8345784842.jpg"
     * 输出 "d:/file/WJ/mini_affa0994987549d19c5c3f8345784842.jpg"
     * @param path
     * @return
     */
    public static String getSltPath(String path){

        String[] result = new String[2];
        path = path.replace('\\','/');
        String  folder = path.substring(0, path.lastIndexOf('/'));
        String filename ="mini_"+path.substring(path.lastIndexOf('/')+1);
        result[0] = folder;
        result[1] = filename;
        return result[0]+"/"+result[1];
    }
    private SltUtils(){}
}
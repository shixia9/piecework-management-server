package com.jackson.common.utils;

import com.jackson.common.exception.RRException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * 获取项目存放路径
 */
@Component
@ConfigurationProperties(prefix = "steel.config")
public class FilePathUtils {
    //图片访问路径
    private static String imgPath;
    //项目根目录
    private static String path;

    //图片存放的目录
    private static String imagePath;
    /**
     * 保存图片
     * @param rootPath 根目录
     * @param suffix 后缀
     * @param bytes 图片字节
     * @param useDateDir 是否使用日期子目录
     * @return 保存后相对目录
     */
//    public static String saveImage(String rootPath, String suffix, byte[] bytes, boolean useDateDir){
//
//        String dateDir = "";
//
//        if(useDateDir) {
//            //日期目录
//            dateDir = DateUtils.format(new Date (), "yyyyMMdd");
//            //判断绝对目录是否存在，不存在则创建
//            File file = new File(rootPath + File.separator +dateDir);
//            if (!file.exists()) {
//                boolean mkdirsFlag = file.mkdirs();
//                if (!mkdirsFlag) {
//                    throw new RRException ("创建日期目录出错");
//                }
//            }
//        }
//        //生成uuid
//        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//        //文件名
//        String fileName = uuid + "." + suffix;
//        //绝对路径
//        String path;
//        if(useDateDir){
//            path = rootPath + File.separator + dateDir + File.separator + fileName;
//        }else {
//            path = rootPath + File.separator + fileName;
//        }
//
//
//        FileImageOutputStream stream = null;
//        try {
//            stream = new FileImageOutputStream(new File(path));
//            stream.write(bytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (stream != null) {
//                    stream.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if(useDateDir){
//            return dateDir + File.separator + fileName;
//        }else {
//            return fileName;
//        }
//    }
    @Deprecated
    public static String getPath(){
        return path;
    }
    public static String getImagePath() {
        return imagePath;
    }
    public static String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        FilePathUtils.imgPath = imgPath;
    }

    //提供给Spring 注入属性用

    public  void setPath(String path) {
        com.jackson.common.utils.FilePathUtils.path = path;
        com.jackson.common.utils.FilePathUtils.imagePath=path+"/images";

        File file = new File(path);
        File imageFile = new File(path+"/images");

        //初始化目录
        if (!file.exists()){
            boolean b = file.mkdirs();
            if(b){ System.out.println("初始化目录："+path); }
        }

        if(!imageFile.exists()){
            boolean b = imageFile.mkdirs();
            if(b){ System.out.println("初始化目录："+path+"/images"); }
        }
    }

}

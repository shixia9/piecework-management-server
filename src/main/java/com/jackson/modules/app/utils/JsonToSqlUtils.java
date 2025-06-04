package com.jackson.modules.app.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jackson.modules.app.pojo.JsonOfAI;
import com.jackson.modules.app.pojo.JsonOfWeb;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据python的返回路径解析文件，返回字符串
 */
public class JsonToSqlUtils {
//    public static String trans(String filePath,String check) {
//
//        if("steel".equals(check)){ //TODO 如果是钢材识别，如何解析字符串
//            String str = "";
//            File file = new File(filePath);
//            //将文件读取成字符串
//            FileInputStream in = null;
//            List<Object> list = null;
//            try{
//                in=new FileInputStream(file);
//                // size 为字串的长度 ，这里一次性读完
//                int size=in.available();
//                byte[] buffer=new byte[size];
//                in.read(buffer);
//                in.close();
//                str=new String(buffer, "GB2312");
//
//                // 拿到解析后的结果
//                list = transSon(str);
//            }catch (IOException e){
//
//            }finally {
//                if (in!=null){
//                    try {
//                        in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            return JSON.toJSONString(list);
//
//        }
////        if("barcode".equals(check)){//TODO 如果是二维码扫描，如何解析结果
////            //从文件夹中读取图片列表
////            File file = new File(filePath);
////            File[] fs = file.listFiles();
////
////            //将图片名，放入一个List
////            List<String> name_type = new ArrayList<>(10);
////            for(File f:fs){
////                if(!f.isDirectory()){
////                    String name = f.getName().split("_")[5]; //文件名
////                    //List 为空，或者List中不包含这个名字
////                    if (name_type.size()==0||!name_type.contains(name)){
////                        name_type.add(name);
////                    }
////                }
////            }
////
////            //List
////
////            List<List<JsonOfWeb>> result = new ArrayList<>(200);
////            int total = 0;
////            int ok = 0;
////            for (int i = 0; i <name_type.size() ; i++) {
////                List<JsonOfWeb> oneImg = new ArrayList<>();
////                for(File f:fs){
////                    if(!f.isDirectory()){
////                        //从文件名中解析出来，宽，高，原点，角度
////                        String[] nameArr = f.getName().split("_");
////                        String name = nameArr[5];
////                        if(name.equals(name_type.get(i))){ //跟图片名对于，放到一个List中
////                            Integer[] point = new Integer[4];
////                            point[0]=(int)Double.parseDouble(""+nameArr[0]);
////                            point[1]=(int)Double.parseDouble(""+nameArr[1]);
////                            point[2]=(int)Double.parseDouble(""+nameArr[2]);
////                            point[3]=(int)Double.parseDouble(""+nameArr[3]);
////
////                            double rotation = Double.parseDouble(""+nameArr[4]);
////                            String barcode = Zxing.decode(f); //用zxing解析出来条码
////
////                            total++; //统计成功率
////                            if(barcode!=null&&!barcode.equals("")){ok++;}
////
////                            JsonOfWeb marker = new JsonOfWeb();
////                            marker.setPoint(point);
////                            marker.setImgName(name);
////                            marker.setRotation(rotation);
////                            marker.setBarcode(barcode);
////                            marker.setStroke("#d71345");
////                            oneImg.add(marker);
////                        }
////
////                    }
////                }
////                result.add(oneImg);
////            }
////            System.out.println("#########################");
////            System.out.println("【成功率】 "+ok/(double)total);
////            System.out.println("#########################");
////           return JSON.toJSONString(result);
////           // 返回解析后的 labelString
////        }
//
//        if("xxx".equals(check)){
//            //TODO 扩展算法 5
//        }
//        return null;
//    }
    //**解析
    public static String transSon(String str) {
        JSONArray array = JSON.parseArray(str);//拿到数组
        List<Object> rootList = new ArrayList<>();
        for (int i = 0; i <array.size() ; i++) {
            List<JsonOfAI> list  = (List<JsonOfAI>)array.get(i); //这里面每一项代表一个图片
            List<JsonOfWeb> webList =  new ArrayList<>(); //用于存放前端的对象
            for (int j = 0; j <list.size() ; j++) {
                JsonOfWeb jow = new JsonOfWeb();
                JSONObject jsonObj = JSON.parseObject(list.get(j)+"");
                jow.setImgName(jsonObj.get("imageName")+"");
                jow.setPoint(getPoint(jsonObj.get("rotateCoordinateInfo")+""));
                jow.setStroke("#d71345");
                jow.setBarcode((String)jsonObj.get("barcode"));
                jow.setRotation(Double.parseDouble(jsonObj.get("angle")+""));
                webList.add(jow);
            }
            rootList.add(webList);
        }
        return JSON.toJSONString(rootList);
    }
    /**
     * 通过四个点坐标计算 宽， 高，旋转。
     */
    private static Integer[] getPoint(String rotateCoordinateInfo) {
        JSONArray jsonArray = JSON.parseArray(rotateCoordinateInfo);
        Integer[] result = new Integer[]{
                (int)Double.parseDouble(jsonArray.get(0) + ""),
                (int)Double.parseDouble(jsonArray.get(1) + ""),
                (int)Double.parseDouble(jsonArray.get(2) + ""),
                (int)Double.parseDouble(jsonArray.get(3) + ""),
                (int)Double.parseDouble(jsonArray.get(4) + ""),
                (int)Double.parseDouble(jsonArray.get(5) + ""),
                (int)Double.parseDouble(jsonArray.get(6) + ""),
                (int)Double.parseDouble(jsonArray.get(7) + "")};
        return result;
    }

}
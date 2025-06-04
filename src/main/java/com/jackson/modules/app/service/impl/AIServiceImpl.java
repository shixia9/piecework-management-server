package com.jackson.modules.app.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.exception.RRException;
import com.jackson.common.utils.*;
import com.jackson.modules.app.dto.HistoryDTO;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.mapper.ImageMapper;
import com.jackson.modules.app.rabbitmq.RabbitmqSend;
import com.jackson.modules.app.rabbitmq.consumer.LandInspectionConsumer;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.app.utils.SltUtils;
import com.jackson.modules.app.utils.UUIDUtil;
import com.jackson.modules.app.vo.ImageVO;
import com.jackson.modules.app.vo.JsonOfWeb;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.service.SteelTypeService;
import com.jackson.modules.sys.controller.AbstractController;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserService;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class AIServiceImpl extends ServiceImpl<ImageMapper, ImageEntity>  implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private SteelTypeService steelTypeService;
    @Autowired
    private RabbitmqSend rabbitmqSend;

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public Long save(MultipartFile multipartFile,Long userId, String check,String picSize,Long steelId) throws IOException {

        //用来查看工件号的基数
        //SteelTypeEntity steel = steelTypeService.getById (steelId);
        SysUserEntity user = sysUserService.getOne(new QueryWrapper<SysUserEntity>()
                .select("enterprise_id")
                .eq("id", userId));

        //查找员工对应公司
        SysEnterpriseEntity enterpriseEntity = enterpriseService.getById(user.getEnterpriseId());
        if (enterpriseEntity.getStorageUsed() >= enterpriseEntity.getStorageLimit()){
            throw new RRException("公司图片储存额度已满,请联系管理员");
        }
        //图片识别保存成功后要修改企业可存储图片容量   此时图片容量存储是四舍五入状态  1.4==> 1.0   1.5==>2.0
        enterpriseService.update(new UpdateWrapper<SysEnterpriseEntity>()
                .set("storage_used",enterpriseEntity.getStorageUsed() + Float.parseFloat(picSize))
                .eq("id",user.getEnterpriseId()));
        ImageEntity imageEntity =  new ImageEntity();

        //获取图片父级目录(已在配置文件里指定)
        String filePath = FilePathUtils.getImagePath();// /work/project/steel/image
        //String fileName = files[i].getOriginalFilename();//neg-x.jpg
        //String houZui = fileName.substring(fileName.lastIndexOf('.')+1);//获取后缀 jpg
        String fileNameUUID = UUIDUtil.getUUID()+".jpg";//生成UUID文件名 81472ffaa2524d3e966bc5cbf8ba0b0d.jpg
        //url为图片绝对地址
        String url =  filePath+'/'+fileNameUUID;//生成路径
        BufferedImage bi = ImageIO.read(multipartFile.getInputStream ());
        if(bi==null){
            throw new RRException("图片格式不符合要求 或 后缀与实际格式不一致;支持(jpg,bmp,gif,png,wbmp,jpeg)",400);
        }
        //1.保存文件
        Date date1 = new Date ();
        multipartFile.transferTo(new File(new File(url).getAbsolutePath()));
        Date date2 = new Date ();
        long time = DateUtils.countSecond (date1, date2);
        logger.info ("图片存储路径为"+url +"  大小: "+ picSize + "MB  耗时: " + time);// /work/project/steel/image/81472ffaa2524d3e966bc5cbf8ba0b0d.jpg
        //创建缩略图
        new Thread(()->{

            try{
                Thumbnails.of(url) //源图片路径
                        .scale(1) //图片宽高不变
                        .outputQuality(0.3) //压缩比 100倍
                        .toFile(SltUtils.getSltPath(url));//压缩后的图片路径
            }catch (IOException e){
                log.error("[ ERROR ] 压缩图片失败 "+url+e);
            }
        }).start();
        //构造一些下面用得到的参数
        imageEntity.setWidth(bi.getWidth()) //插入数据库用
                .setHeight(bi.getHeight())
                .setUrl(url)
                .setUserId(userId)
                .setName(fileNameUUID)
                .setSteelId(steelId)
                .setCheck(check)
                .setCreateTime (new Date ())
                .setEnterpriseId(user.getEnterpriseId());
        //TODO 此时完成单条数据的部分属性赋值 剩余的看LandInspectionConsumer对rabbitMQ的消息消费
        this.save (imageEntity);
        JSONObject jsonObject = new JSONObject ();
        //jsonObject.put ("url",DatatypeConverter.printBase64Binary (bytes));
        jsonObject.put ("url",FilePathUtils.getImgPath ()+fileNameUUID);
        jsonObject.put ("imgName",fileNameUUID);
        rabbitmqSend.sendToLandInpection (JSON.toJSONString (jsonObject));
        return imageEntity.getId();
    }

    @Override
    public R reSave(Long imageId, Long userId) {
        //获取图片父级目录(已在配置文件里指定)
        String filePath = FilePathUtils.getImgPath ();

        ImageEntity image = this.getById (imageId);
        //url为图片绝对地址
        String url =  filePath+'/'+image.getName ();//生成路径

        // TODO 调用算法接口
        JSONObject jsonObject = new JSONObject ();
        jsonObject.put ("url",url);
        jsonObject.put ("imgName",image.getName ());

        rabbitmqSend.sendToLandInpection (JSON.toJSONString (jsonObject));

        return R.ok ();
    }



    @Override
    public List<ImageEntity> queryHistory(HistoryDTO historyDTO, Long userId, String check){
        // g 是 greater 大； l 是 less 小  e是equal
        List<ImageEntity> list = this.list(
                new QueryWrapper<ImageEntity>()
                        .select("id", "url", "label_string","name","steel_id", "create_time", "num")
                        .eq("user_id",userId)
                        .eq("`check`",check)
                        .eq (historyDTO.getSteelId () != null,"steel_id",historyDTO.getSteelId ())
                        .ge(historyDTO.getStartTime() != null,"create_time",historyDTO.getStartTime())
                        .le(historyDTO.getEndTime() != null,"create_time",historyDTO.getEndTime())
                        .orderByDesc("create_time")
        );
        for (ImageEntity image: list) {
            List<JsonOfWeb> labelStringList = null;
            SteelTypeEntity steel = steelTypeService.getById (image.getSteelId());
            //TODO 此段是因为安卓端需要变更labelString返回形式
            JSONArray labelString = JSON.parseArray(image.getLabelString());
            if (labelString != null){
                 labelStringList = new ArrayList(labelString);
            }
            image.setLabelStringList(labelStringList);
            image.setSteelTypeName (steel.getName ());
            image.setLabelString(null);
        }
        return list;
    }

    @Override
    public ImageEntity queryById(Long id, Long userId, String check) {
        ImageEntity imageEntity = this.getOne(new QueryWrapper<ImageEntity>()
                .eq("id",id)
                .eq("user_id", userId)
                .eq("`check`", check));
        SteelTypeEntity steel = steelTypeService.getById (imageEntity.getSteelId());

        //TODO 此段是因为安卓端需要变更labelString返回形式
        JSONArray labelString = JSON.parseArray(imageEntity.getLabelString());
        List<JsonOfWeb> labelStringList = null;
        if (labelString != null){
            labelStringList = new ArrayList(labelString);
        }
        imageEntity.setSteelTypeName (steel.getName ());
        imageEntity.setLabelStringList(labelStringList);
        imageEntity.setLabelString(null);
        return imageEntity;
    }

    @Override
    public byte[] getImage(String name, HttpServletResponse response, HttpServletRequest request,Long userId) {

        //缓存时间 单位分钟
        final int cacheTime = 10 ;

        if (StringUtils.isBlank(name)){
            throw new RRException("图片名不能为空");
        }

        //图片被删除立即过期，图片无法被修改，我们不提供这个接口。 程序也没有这个行为
        File imageFile = new File(FilePathUtils.getImagePath()+"/"+name);

        if(!imageFile.exists())throw new RRException("图片不存在");
        try{
            String header = request.getHeader("If-Modified-Since");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Long suBTime = -1L;
            if(header!=null){
                Date parse = sdf.parse(header);
                suBTime = parse.getTime()-System.currentTimeMillis();
            }
            if(suBTime>0){
                System.out.println(name+"还剩"+suBTime/(60*1000)+"分钟过期");
                response.setStatus(304);
                return null;
            }else{
                System.out.println(name+"已过期");
                response.setHeader("Cache-Control","private");
                response.setHeader("Last-Modified",new Date(System.currentTimeMillis()+1000*60*cacheTime)+"");
            }
        }catch (Exception e){
            System.out.println("日期格式 解析失败！");
            e.printStackTrace();
        }

        FileInputStream fis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            fis = new FileInputStream(imageFile);
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("未知异常");
        } finally {
            try {
                if (os!=null){ os.close(); }
                if(fis!=null){ fis.close(); }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[1];
    }


}

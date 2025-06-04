package com.jackson.modules.app.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.rabbitmq.RabbitmqConstant;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.app.utils.JsonToSqlUtils;
import com.jackson.modules.app.vo.ImageVO;
import com.jackson.modules.app.vo.JsonOfWeb;
import com.jackson.modules.enterprise.service.SteelTypeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 图片识别算法 队列接收
 */
@Component
public class LandInspectionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LandInspectionConsumer.class);
    @Autowired
    private AIService aiService;
    @Autowired
    private SteelTypeService steelTypeService;


    /**
     * 推（push）：MQ 主动将消息推送给消费者，这种方式需要消费者设置一个缓冲区去缓存消息，
     * 对于消费者而言，内存中总是有一堆需要处理的消息，所以这种方式的效率比较高，这也是目前大多数应用采用的消费方式。
     * @param message
     */
    @RabbitListener(queues = "land_inspection_response")
    public void received(String message){
        logger.info("land_inspection_response message:"+message);
        if (StringUtils.isNotBlank(message)){
            JSONArray images = JSON.parseArray(JsonToSqlUtils.transSon (message)); //本质是List

            //匹配文件名。 得到单张图片的
            for (int j = 0; j < images.size (); j++) {
                //单张图片的数据
                //JSONArray array1 = JSON.parseArray(JSON.toJSONString(array.get(j)));
                JSONArray image = (JSONArray) images.get(j);
                //ofWeb : {"imgName":"c8b72e689cc34e6b8e0eeb57205b2e5a.jpg","rotation":174.09,"stroke":"#d71345","point":[231,495,292,490,287,431,225,437]}
                //拿单条数据作为对象 目的是拿到返回的图片名 好一一对应
                JsonOfWeb ofWeb = JSON.parseObject(JSON.toJSONString(image.get(0)), JsonOfWeb.class);
                String imgName = ofWeb.getImgName();
                ImageEntity one = aiService.getOne (new QueryWrapper<ImageEntity> ().eq ("`name`", imgName));
                aiService.update (new UpdateWrapper<ImageEntity> ()
                        .eq ("`name`",imgName)
                        .set ("label_string",JSON.toJSONString(image))
                        .set ("num",steelTypeService.getById (one.getSteelId ()).getBaseNum () * image.size())//array1.size()为框的数量  num初始值为钢材基数
                        .set ("update_time",new Date ()));
            }
        }
    }
}

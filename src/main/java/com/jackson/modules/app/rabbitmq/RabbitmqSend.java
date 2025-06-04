package com.jackson.modules.app.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq消息发送
 */
@Component
public class RabbitmqSend {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqSend.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToLandInpection(String message){
        rabbitTemplate.convertAndSend(RabbitmqConstant.QueueName.LAND_INSPECTION_REQUEST.getValue(),message);
        logger.info("land_inspection_request message已发送");
    }

}

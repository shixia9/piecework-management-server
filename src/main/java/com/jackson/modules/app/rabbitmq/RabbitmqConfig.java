package com.jackson.modules.app.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class RabbitmqConfig {

    @Bean
    public Queue queue_land_inpection_request(){
        return new Queue(RabbitmqConstant.QueueName.LAND_INSPECTION_REQUEST.getValue());
    }

    @Bean
    public Queue queue_land_inpection_response(){
        return new Queue(RabbitmqConstant.QueueName.LAND_INSPECTION_RESPONSE.getValue());
    }
}

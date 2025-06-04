package com.jackson.modules.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.rabbitmq.RabbitmqSend;
import com.jackson.modules.app.rabbitmq.consumer.LandInspectionConsumer;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.app.utils.JsonToSqlUtils;
import com.jackson.modules.app.vo.ImageVO;
import com.jackson.modules.app.vo.JsonOfWeb;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springfox.documentation.spring.web.json.Json;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {

    @org.junit.jupiter.api.Test
    public void test01() {

    }


}

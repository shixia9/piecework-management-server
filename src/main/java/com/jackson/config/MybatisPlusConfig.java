package com.jackson.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * mybatis-plus配置
 */
@Configuration
public class MybatisPlusConfig {
//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        //2.该处就是指定的路径(需要提前创建好目录，否则上传时会抛出异常)
//        factory.setLocation("/work/project/steel");
//        return factory.createMultipartConfig();
//    }
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}

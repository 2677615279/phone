package com.past;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan(basePackages = {"com.past.dao"}) // 扫描mybatis的接口映射
@ServletComponentScan(basePackages = {"com.past.common"}) // 配置使用servlet相关注解的类能被 springboot 扫描到
@EnableCaching // 开启缓存注解的支持
@EnableSwagger2Doc // 开启swagger文档的支持
@EnableAsync // 开启异步的支持
public class PhoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneApplication.class, args);
    }

}

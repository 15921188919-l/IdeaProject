package com.tensquare.base;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 基础微服务的启动类
 */
@SpringBootApplication
@EnableEurekaClient
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class,args);
    }

    /**
     * 初始化IdWorder对象
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }
}

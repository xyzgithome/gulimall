package com.atguigu.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、开启服务注册发现
 *  (配置nacos的注册中心地址)
 * 2、编写网关配置文件
 * 3、@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 * 由于网关没有用到mybatis相关依赖，所以没配置数据库信息，会报错，加上以上exclude可以消除项目启动报错
 * pom中exclusive common中的mybatis依赖也可消除项目启动报错
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

}

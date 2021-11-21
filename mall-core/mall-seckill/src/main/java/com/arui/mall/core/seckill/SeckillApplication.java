package com.arui.mall.core.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ...
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.arui.mall.core.seckill.mapper")
@EnableFeignClients(basePackages = "com.arui.mall.feign.client")
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}

package com.arui.mall.core.payment;

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
@MapperScan(basePackages = "com.arui.mall.core.payment.mapper")
@EnableFeignClients(basePackages = "com.arui.mall.feign.client")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}

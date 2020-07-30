package com.avengereug.mall.product;

import com.avengereug.mall.common.anno.StartApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@StartApplication
@EnableFeignClients(basePackages = {"com.avengereug.mall.coupon.feign"})
public class ServiceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }

}

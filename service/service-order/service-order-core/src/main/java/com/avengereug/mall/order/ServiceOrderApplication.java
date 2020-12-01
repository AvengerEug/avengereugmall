package com.avengereug.mall.order;

import com.avengereug.mall.common.anno.EnableFeignConfig;
import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@StartApplication
@EnableRabbit
@EnableRedisHttpSession
@EnableFeignClients(basePackages = {
        "com.avengereug.mall.member.feign",
        "com.avengereug.mall.cart.client",
        "com.avengereug.mall.warehouse.feign",
        "com.avengereug.mall.product.feign"
})
@EnableFeignConfig
public class ServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }

}

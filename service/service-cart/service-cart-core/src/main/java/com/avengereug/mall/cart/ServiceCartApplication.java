package com.avengereug.mall.cart;

import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@StartApplication
@EnableFeignClients(
        basePackages = {
                "com.avengereug.mall.product.feign"
        }
)
public class ServiceCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication.class, args);
    }
}

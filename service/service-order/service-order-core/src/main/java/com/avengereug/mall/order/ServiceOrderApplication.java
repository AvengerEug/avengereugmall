package com.avengereug.mall.order;

import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@StartApplication
@EnableRabbit
@EnableRedisHttpSession
public class ServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }

}

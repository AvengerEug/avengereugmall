package com.avengereug.mall.order;

import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;

@StartApplication
@EnableRabbit
public class ServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }

}

package com.avengereug.mall.gateway;

import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@StartApplication
public class ApiGateWayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayServerApplication.class, args);
    }
}

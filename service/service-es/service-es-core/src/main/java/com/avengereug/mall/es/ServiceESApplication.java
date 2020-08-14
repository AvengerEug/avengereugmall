package com.avengereug.mall.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "com.avengereug.mall.product.feign"
})
@SpringBootApplication
public class ServiceESApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceESApplication.class, args);
    }
}

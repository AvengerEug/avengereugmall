package com.avengereug.mall.member;

import com.avengereug.mall.common.anno.StartApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "com.avengereug.mall.order.feign",
        "com.avengereug.mall.warehouse.feign",
        "com.avengereug.mall.product.feign",
        "com.avengereug.mall.coupon.feign"
})
@StartApplication
public class ServiceMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMemberApplication.class, args);
    }

}

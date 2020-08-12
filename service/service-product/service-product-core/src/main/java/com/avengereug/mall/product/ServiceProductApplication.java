package com.avengereug.mall.product;

import com.avengereug.mall.common.anno.StartApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableFeignClients(basePackages = {
        "com.avengereug.mall.coupon.feign",
        "com.avengereug.mall.warehouse.feign",
        "com.avengereud.mall.es.client"
})
@MapperScan("com.avengereug.mall.product.dao")
@StartApplication
public class ServiceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }

}

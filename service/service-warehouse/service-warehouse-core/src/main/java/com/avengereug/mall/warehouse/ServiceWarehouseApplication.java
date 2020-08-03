package com.avengereug.mall.warehouse;

import com.avengereug.mall.common.anno.StartApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableFeignClients("com.avengereug.mall.product.feign")
@MapperScan("com.avengereug.mall.warehouse.dao")
@StartApplication
public class ServiceWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceWarehouseApplication.class, args);
    }

}

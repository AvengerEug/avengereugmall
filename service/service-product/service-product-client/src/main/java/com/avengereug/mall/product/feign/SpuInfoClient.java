package com.avengereug.mall.product.feign;

import com.avengereug.mall.product.entity.SpuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-product", contextId = "spuClient")
@RequestMapping("product/spuinfo")
public interface SpuInfoClient {


    @GetMapping("/skuId")
    SpuInfoEntity queryBySkuId(@RequestParam("skuId") Long skuId);

}

package com.avengereug.mall.product.feign;

import com.avengereug.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("service-product")
@RequestMapping("product/category")
public interface CategoryClient {

    @GetMapping("/list")
    R list(@RequestParam Map<String, Object> params);
}

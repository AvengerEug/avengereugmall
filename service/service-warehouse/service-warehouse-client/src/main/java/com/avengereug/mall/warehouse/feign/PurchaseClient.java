package com.avengereug.mall.warehouse.feign;

import com.avengereug.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-warehouse", contextId = "purchaseClient")
@RequestMapping("warehouse/purchase")
public interface PurchaseClient {

    @GetMapping("/list")
    R list(@RequestParam Map<String, Object> params);
}

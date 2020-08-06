package com.avengereug.mall.warehouse.feign;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "service-warehouse", contextId = "wareSkuClient")
@RequestMapping("warehouse/waresku")
public interface WareSkuClient {

    @GetMapping("/inner/has-stock/{skuId}")
    RPCResult<Boolean> innerHasStock(@PathVariable("skuId") Long skuId);
}

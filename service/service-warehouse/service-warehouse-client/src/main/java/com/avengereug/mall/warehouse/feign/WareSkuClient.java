package com.avengereug.mall.warehouse.feign;

import com.avengereug.mall.common.utils.RPCResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-warehouse", contextId = "wareSkuClient")
@RequestMapping("warehouse/waresku")
public interface WareSkuClient {

    @PostMapping("/inner/has-stock")
    RPCResult<Map<Long, Boolean>> innerHasStock(@RequestBody List<Long> skuIds);
}

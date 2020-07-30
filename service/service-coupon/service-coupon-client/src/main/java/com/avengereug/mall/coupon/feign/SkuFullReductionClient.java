package com.avengereug.mall.coupon.feign;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.coupon.to.SkuFullReductionTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-coupon", contextId = "skuFullReductionClient")
@RequestMapping("/coupon/skufullreduction")
public interface SkuFullReductionClient {

    @PostMapping("/save/inner")
    R saveInner(@RequestBody SkuFullReductionTO skuFullReductionTo);
}

package com.avengereug.mall.coupon.feign;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.coupon.to.SkuLadderTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-coupon", contextId = "skuLadderClient")
@RequestMapping("coupon/skuladder")
public interface SkuLadderClient {

    @PostMapping("/inner/save")
    R innerSave(@RequestBody SkuLadderTO skuLadderTo);
}

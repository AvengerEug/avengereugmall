package com.avengereug.mall.coupon.feign;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.coupon.to.SpuBoundsTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-coupon", contextId = "spuBoundsClient")
@RequestMapping("coupon/spubounds")
public interface SpuBoundsClient {

    @PostMapping("/inner/save")
    R innerSave(@RequestBody SpuBoundsTO spuBoundsTO);
}

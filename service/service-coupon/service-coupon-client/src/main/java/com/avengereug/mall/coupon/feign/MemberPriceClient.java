package com.avengereug.mall.coupon.feign;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.coupon.to.MemberPriceTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "service-coupon", contextId = "memberPriceClient")
@RequestMapping("coupon/memberprice")
public interface MemberPriceClient {


    @PostMapping("/save/inner")
    R saveBatchInner(@RequestBody List<MemberPriceTO> memberPriceTo);
}

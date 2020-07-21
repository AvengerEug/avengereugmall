package com.avengereug.mall.member.controller;

import com.avengereug.mall.common.utils.Constant;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.coupon.feign.CouponClient;
import com.avengereug.mall.order.feign.OrderClient;
import com.avengereug.mall.product.feign.CategoryClient;
import com.avengereug.mall.warehouse.feign.PurchaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-feign")
public class TestFeignController {


    @Autowired
    private CouponClient couponClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private PurchaseClient purchaseClient;

    @GetMapping
    public R test() {

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.PAGE, 1);
        map.put(Constant.LIMIT, 5);

        R list = couponClient.list(map);
        R list1 = orderClient.list(map);
        R list2 = categoryClient.list(map);
        R list3 = purchaseClient.list(map);


        return R.ok().put("coupon", list.get("page")).put("order", list1.get("page")).put("category", list2.get("page")).put("purchase", list3);
    }
}

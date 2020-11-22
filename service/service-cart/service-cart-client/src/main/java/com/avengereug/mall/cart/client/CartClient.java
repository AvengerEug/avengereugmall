package com.avengereug.mall.cart.client;

import com.avengereug.mall.cart.vo.CartItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "service-cart", contextId = "cartClient")
public interface CartClient {


    @GetMapping(value = "/currentUserCartItems")
    List<CartItemVo> getCurrentCartItems();

}

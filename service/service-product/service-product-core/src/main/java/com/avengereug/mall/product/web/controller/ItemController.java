package com.avengereug.mall.product.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {

    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable(name = "skuId") Long skuId) {
        System.out.println("skuId = " + skuId);
        return "item";
    }
}

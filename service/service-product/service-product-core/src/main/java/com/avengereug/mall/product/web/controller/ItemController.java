package com.avengereug.mall.product.web.controller;

import com.avengereug.mall.product.service.SkuInfoService;
import com.avengereug.mall.product.vo.SkuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable(name = "skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVO skuItemVO = skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVO);
        return "item";
    }
}

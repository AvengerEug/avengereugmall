package com.avengereug.mall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("orderWebController")
public class OrderController {


    @GetMapping("/toTrade")
    public String toTrade() {
        return "confirm";
    }
}

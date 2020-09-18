package com.avengereug.mall.thirdparty.controller;

import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.thirdparty.components.SMSComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SMSController {

    @Autowired
    private SMSComponent smsComponent;

    @GetMapping("/send")
    public RPCResult<Boolean> send(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendCode(phone, code);
        return new RPCResult<Boolean>().ok(true);
    }
}

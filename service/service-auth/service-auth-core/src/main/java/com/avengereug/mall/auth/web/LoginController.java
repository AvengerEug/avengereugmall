package com.avengereug.mall.auth.web;

import com.avengereug.mall.auth.feign.SMSClient;
import com.avengereug.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private SMSClient smsClient;

    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

//        smsClient.send(phone, "123");
        return R.ok();
    }

}

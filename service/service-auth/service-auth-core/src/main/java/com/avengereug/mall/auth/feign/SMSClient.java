package com.avengereug.mall.auth.feign;

import com.avengereug.mall.common.utils.RPCResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-thirdparty", contextId = "smsClient")
@RequestMapping("/sms")
public interface SMSClient {

    @GetMapping("/send")
    RPCResult<Boolean> send(@RequestParam("phone") String phone, @RequestParam("code") String code);
}

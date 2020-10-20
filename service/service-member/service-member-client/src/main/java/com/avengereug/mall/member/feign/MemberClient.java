package com.avengereug.mall.member.feign;

import com.avengereug.mall.auth.common.vo.UserLoginVo;
import com.avengereug.mall.auth.common.vo.UserRegisterVo;
import com.avengereug.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "service-member", contextId = "memberClient")
@RequestMapping("member/member")
public interface MemberClient {

    @PostMapping(value = "/register")
    R register(@RequestBody UserRegisterVo vo);

    @PostMapping(value = "/login")
    R login(@RequestBody UserLoginVo vo);
}

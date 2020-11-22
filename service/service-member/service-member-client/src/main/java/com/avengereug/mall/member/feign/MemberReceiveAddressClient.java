package com.avengereug.mall.member.feign;

import com.avengereug.mall.member.entity.MemberReceiveAddressEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "service-member", contextId = "memberReceiveAddressClient")
@RequestMapping("member/memberreceiveaddress")
public interface MemberReceiveAddressClient {

    @GetMapping("/{memberId}/address")
    List<MemberReceiveAddressEntity> getAddress(@PathVariable("memberId") Long memberId);

}

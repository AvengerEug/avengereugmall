package com.avengereug.mall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("service-member")
public interface MemberClient {


}

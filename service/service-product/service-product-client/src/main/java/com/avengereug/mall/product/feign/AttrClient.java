package com.avengereug.mall.product.feign;

import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.product.vo.AttrRespVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "service-product", contextId = "attrClient")
@RequestMapping("/product/attr")
public interface AttrClient {


    @GetMapping("/inner/info/{attrId}")
    RPCResult<AttrRespVO> attrInfo(@PathVariable("attrId") Long attrId);

}

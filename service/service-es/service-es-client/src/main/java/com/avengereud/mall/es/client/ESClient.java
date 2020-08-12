package com.avengereud.mall.es.client;

import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.es.to.SpuESTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "service-es", contextId = "esClient")
@RequestMapping("/es")
public interface ESClient {

    @PostMapping("/index-sku-document")
    RPCResult<Boolean> indexSpu(@RequestBody List<SpuESTO> spuESTOList);
}

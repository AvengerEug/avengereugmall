package com.avengereug.mall.es.controller;

import com.avengereug.mall.common.controller.BaseController;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.es.service.ESService;
import com.avengereug.mall.to.SpuESTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/es")
public class ESController extends BaseController {


    @Autowired
    private ESService esService;

    /**
     * 上架商品api ==> 索引spu文档至product索引内
     * @return
     */
    @PostMapping("/index-sku-document")
    public RPCResult<Boolean> indexSpu(@RequestBody List<SpuESTO> spuESTOList) throws IOException {
        Boolean result = esService.indexSpu(spuESTOList);

        return new RPCResult<Boolean>().ok(result);
    }
}

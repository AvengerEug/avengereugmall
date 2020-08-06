package com.avengereug.mall.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.avengereug.mall.es.config.ElasticsearchConfig;
import com.avengereug.mall.es.constants.ESConstants;
import com.avengereug.mall.es.service.ESService;
import com.avengereug.mall.to.SpuESTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ESServiceImpl implements ESService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public Boolean indexSpu(List<SpuESTO> spuESTOList) throws IOException {
        // 1、创建product索引(提前使用kibana在es中创建索引)

        // 2、创建文档映射关系(提前使用kibana在es中创建mapping)

        // 3、批量索引文档至es，创建IndexRequest往bulkRequest里面插
        // 参考官方文档： https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-bulk.html
        BulkRequest bulkRequest = new BulkRequest();
        for (SpuESTO spuESTO : spuESTOList) {
            String jsonString = JSON.toJSONString(spuESTO);
            IndexRequest indexRequest = new IndexRequest(ESConstants.INDEX_PRODUCT);
            indexRequest.id(spuESTO.getSkuId().toString()).source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        // TODO 处理错误
        if (bulk.hasFailures()) {
            log.error("ES: 商品上架错误，{}", bulk.getItems().toString());
        }
        return !bulk.hasFailures();
    }
}

package com.avengereug.mall.es;

import com.alibaba.fastjson.JSON;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.es.config.ElasticsearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@SpringBootTest
public class ServiceESApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {

        System.out.println(client);
    }

    @Test
    void indexDocument() throws IOException {

        IndexRequest indexRequest = new IndexRequest("avengereug");
        // api中所有的id都为字符串类型，如果不显示指定id，则由es生成
        indexRequest.id("Yi9-wXMBjWrozUakddMr");
        User user = new User();
        // 此id为user对象的id，并不是文档的id
        user.setId(1L);
        user.setGender("M");
        user.setAge(18);

        String jsonString = JSON.toJSONString(user);

        // 指定提交的数据是一个json字符串
        indexRequest.source(jsonString, XContentType.JSON);

        System.out.println(client.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS));
    }


    @Data
    private class User {
        private Long id;
        private String gender;
        private Integer age;
    }

    /**
     * 测试复杂检索
     * 全文检索出地址中包含mill的数据，并将这些数据按照年龄聚合，
     * 同时求这些年龄段的人的平均薪资(嵌套聚合)。以及地址中包含mill的平均年龄、平均薪资
     *
     */
    @Test
    void dslSearch() throws IOException {
        // 1、构建查询请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");

        // 2、构建查询条件(对应原生es中的query对象)
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        searchSourceBuilder
                .aggregation(AggregationBuilders.avg("ageAvg").field("age"))
                .aggregation(AggregationBuilders.avg("balanceAvg").field("balance"))
                .aggregation(
                        AggregationBuilders.terms("countGroupByAge").field("age").size(10)
                                .subAggregation(
                                        AggregationBuilders.avg("balanceAvg").field("balance")
                                )
                );

        System.out.println("查询条件： " + searchSourceBuilder);

        // 4、查询请求与查询条件绑定
        // TODO: 缺少了这一步，发现相同的查询条件在kibana中查询是正确的，而在此是错误的。导致花了大量时间查找原因
        searchRequest.source(searchSourceBuilder);


        // 5、发送请求至ES
        SearchResponse response = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

        // 6、获取文档基本信息
        SearchHits hits = response.getHits();
        System.out.println("检索到的数量：" + hits.getTotalHits().value);
        System.out.println("检索到的数据中相关性得分最大值为：" + hits.getMaxScore());

        for (SearchHit hit : hits.getHits()) {

            String hitJSONString = hit.getSourceAsString();
            Account account = JSON.parseObject(hitJSONString, Account.class);
            System.out.println(account);

        }

        // 7、获取文档聚合信息
        // 7.1 获取根据查看平均年龄，年龄段平均薪资
        Aggregations aggregations = response.getAggregations();
        Avg avg = aggregations.get("ageAvg");
        System.out.println("平均年龄：" + avg.getValue());

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAvg.getValue());

        Terms countGroupByAge = aggregations.get("countGroupByAge");
        for (Terms.Bucket bucket : countGroupByAge.getBuckets()) {
            System.out.println("年龄分布：" + bucket.getKeyAsString() +
                    ", 人数：" + bucket.getDocCount() +
                    ", 平均年龄：" + ((Avg)bucket.getAggregations().get("balanceAvg")).getValue());

        }


    }


    @ToString
    @Data
    private static class Account {
        private Long account_number;

        private Long balance;

        private String firstname;

        private String lastname;

        private Integer age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;
    }
}

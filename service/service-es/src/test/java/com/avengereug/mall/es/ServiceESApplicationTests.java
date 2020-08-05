package com.avengereug.mall.es;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class ServiceESApplicationTests {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Test
    void contextLoads() {
        System.out.println(highLevelClient);
    }
}

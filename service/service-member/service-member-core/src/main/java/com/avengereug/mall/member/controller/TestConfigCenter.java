package com.avengereug.mall.member.controller;

import com.avengereug.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-config")
public class TestConfigCenter {

    @Autowired
    private TestEntity testEntity;

    @GetMapping
    public R test() {
        return R.ok().put("entity", testEntity);
    }


    /**
     * Nacos启动时，默认会读取 group为DEFAULT_GROUP的service-member.properties(service-member.yml)文件(dataId)
     *
     *
     */
    @Component
    @ConfigurationProperties("test.config")
    class TestEntity {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}

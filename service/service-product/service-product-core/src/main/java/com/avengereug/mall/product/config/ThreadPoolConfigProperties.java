package com.avengereug.mall.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "avengermall.product.thread.conf")
@Component
public class ThreadPoolConfigProperties {

    private Integer coreSize;
    private Integer threadSize;
    private Integer queueSize;

    public Integer getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(Integer coreSize) {
        this.coreSize = coreSize;
    }

    public Integer getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(Integer threadSize) {
        this.threadSize = threadSize;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}

package com.avengereug.mall.product.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@EnableCaching
@Configuration
public class SpringCacheConfig {

    @Bean
    public RedisCacheConfiguration redisConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 设置value的序列化为标准的json序列化格式
        config = config.serializeValuesWith(
                RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 设置两分钟过期
        config.entryTtl(Duration.ofMillis(2));

        return config;
    }

}

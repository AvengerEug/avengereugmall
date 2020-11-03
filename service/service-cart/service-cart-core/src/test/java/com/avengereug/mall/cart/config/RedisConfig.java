package com.avengereug.mall.cart.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    /**
     * GenericFastJsonRedisSerializer序列化方式需要保证序列化对象有无参构造方法以及对各个属性的set/get方法
     * 因为反序列化时，就是先根据type创建出来对象，然后在获取到它的属性，并根据它的set、get方法进行填充属性，
     * 完成反序列化的操作
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setHashKeySerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setKeySerializer(new GenericFastJsonRedisSerializer());

        return redisTemplate;
    }
}

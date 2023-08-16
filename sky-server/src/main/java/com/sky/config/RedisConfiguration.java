package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 序列化：将java对象存储为字节码，方便跨平台使用java对象
 * 反序列化：将字节码反序列化为java对象，String,json等等
 */
@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始进行Redis信息存储");
        RedisTemplate redisTemplate=new RedisTemplate();
        //设置redisTemplate的连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redisTemplate的key的序列化器(字符串序列化器）
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}

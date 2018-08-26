package com.yq.config;

import com.yq.controller.RedisController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Simple to Introduction
 * className: MyRedisConfig
 *
 * @author EricYang
 * @version 2018/8/5 23:05
 */

public class MyRedisConfig {
    private Logger logger = LoggerFactory.getLogger(MyRedisConfig.class);
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        logger.info("myConfig LettuceConnectionFactory");
        return connectionFactory; // <2>
    }

//    @Bean
//    public RedisConnectionFactory connectionFactory2() {
//        RedisStandaloneConfiguration connectionConfig = new RedisStandaloneConfiguration();
//        connectionConfig.setDatabase(2);
//        connectionConfig.setHostName("127.0.0.1");
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(connectionConfig);
//        return connectionFactory;
//    }
}
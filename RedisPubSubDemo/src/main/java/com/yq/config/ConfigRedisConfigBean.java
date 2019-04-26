package com.yq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Simple to Introduction
 * className: ConfigA
 *
 * @author EricYang
 * @version 2018/10/21 15:39
 */
@Configuration
@Order(3)
@Slf4j
public class ConfigRedisConfigBean {

    @Bean
    public RedisConfig redisConfig() {
        log.info("Create a redisConfig");
        return new RedisConfig();
    }
}


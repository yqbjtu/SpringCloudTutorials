package com.yq.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Simple to Introduction
 * className: MyRedisConfig
 *
 * @author EricYang
 * @version 2019/2/23 12:31
 */
public class MyRedisConfig {
    @Bean
    LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions options, ClientResources dcr){
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(new GenericObjectPoolConfig())
                .clientOptions(options)
                .clientResources(dcr)
                .build();
    }

    @Bean
    public LettuceConnectionFactory connectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                    LettucePoolingClientConfiguration lettucePoolConfig) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

//    @Bean
//    LettuceConnectionFactory lettuceClientConfigurationConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisHostName);
//        redisStandaloneConfiguration.setPort(redisPort);
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisAuth));
//        redisStandaloneConfiguration.setDatabase(0);
//
//        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfiguration = LettuceClientConfiguration.builder();
//
//        lettuceClientConfiguration.useSsl();
//
//        LettuceConnectionFactory lettuceConFactory = new LettuceConnectionFactory(redisStandaloneConfiguration,
//                lettuceClientConfiguration.build());
//
//        return lettuceConFactory;
//    }
}


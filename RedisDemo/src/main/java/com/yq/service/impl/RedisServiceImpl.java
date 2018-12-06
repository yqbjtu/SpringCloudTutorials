package com.yq.service.impl;

import com.yq.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Simple to Introduction
 * className: RedisServiceImpl
 *
 * @author EricYang
 * @version 2018/8/4 23:00
 */
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    //private RedisTemplate<String, String> template;
    private StringRedisTemplate template;

    @Autowired
    RedisAtomicLong redisAtomicLong;

    private static final String LONG_KEY = "yqLong";

    @Bean
    public RedisAtomicLong getRedisAtomicLong() {
        RedisAtomicLong counter = new RedisAtomicLong(LONG_KEY, template.getConnectionFactory());
        return counter;
    }

    @Override
    public String get(String key) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        return ops.get(key);
    }

    @Override
    public Boolean del(String key) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        String value = ops.get(key);
        return template.delete(key);
    }

    @Override
    public int delByPattern(String keyPattern) {
        Set<String>  keys = template.keys(keyPattern);
        ValueOperations<String, String> ops = this.template.opsForValue();
        int count = keys.size();
        keys.forEach(new Consumer<String>() {
            @Override
            public void accept(String key) {
                String value = ops.get(key);
                template.delete(key);
            }
        });

        return count;
    }

    @Override
    public void set(String key, String value) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        ops.set(key, value);
    }

    @Override
    public String getHash(String key, String hashKey) {
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        return hashOps.get(key, hashKey);
    }

    @Override
    public void setHash(String key, String hashKey, String value) {
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        hashOps.put(key, hashKey, value);
    }

    @Override
    public long getRedisSequence() {
        long sequence = 0L;
        try {
            if (redisAtomicLong.get() == 0) {
                redisAtomicLong.getAndSet(0L);
            }
            sequence = redisAtomicLong.incrementAndGet();
        } catch (Exception ex) {
            log.error("Failed to get sequence.", ex);
        }
        return sequence;
    }
}



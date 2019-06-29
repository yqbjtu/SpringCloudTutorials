package com.yq.service.impl;

import com.yq.config.RedisConfig;
import com.yq.dist.DistLock;
import com.yq.service.RedisService;
import com.yq.task.RunnableTask;
import org.redisson.Redisson;
import org.redisson.api.RExecutorService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Simple to Introduction
 * className: RedisServiceImpl
 *
 * @author EricYang
 * @version 2018/8/4 23:00
 */
@Service
@Order(5)
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private DistLock distLock;

    @Autowired
    private RedisConfig redisConfig;

    private RMap<String, String> map;

    RedisServiceImpl() {
        Config config=new Config();
//        config.useSingleServer()
//                .setAddress(redisConfig.getRedisHost() + ":" + redisConfig.getRedisPort());
        //.setPassword(redisConfig.getPassword());
        config.useSingleServer()
                .setAddress("127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);

        RExecutorService executorService = redisson.getExecutorService("myExecutor");

        map = redisson.getMap("key1");

        executorService.submit(new RunnableTask(100));

    }


    @Override
    public RMap getMap(String key) {
        return map;
    }

    @Override
    public RMap mapAddEntry(String key, String value) {
        map.put(key, value);
        return map;
    }

    @Override
    public String mapDelEntry(String key) {
        String value = map.remove(key);
        return value;
    }

    @Override
    public String get(String key) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        return ops.get(key);
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
}



package com.yq.dist;

import com.yq.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Simple to Introduction
 * className: DistLock
 *
 * @author EricYang
 * @version 2019/4/26 9:29
 */
@Service
@Slf4j
public class DistLock {
    @Autowired
    private RedisConfig redisConfig;

    private RedissonClient redisson;

    public DistLock() {
        log.info("DistLock constructor {}", redisConfig);
    }

    public boolean init() {
        Config config=new Config();
        config.useSingleServer()
                .setAddress(redisConfig.getRedisHost() + ":" + redisConfig.getRedisPort());
        //.setPassword(redisConfig.getPassword());

        redisson= Redisson.create(config);

        return true;
    }
    /**
     * 获取锁
     * @param objectName
     * @return
     */
    public RLock getRLock(String objectName){
        RLock rLock = redisson.getLock(objectName);
        return rLock;
    }

    public RMap<String, String> getRMap(String objectName){
        RMap<String, String>  rMap = redisson.getMap(objectName);
        return rMap;
    }

}

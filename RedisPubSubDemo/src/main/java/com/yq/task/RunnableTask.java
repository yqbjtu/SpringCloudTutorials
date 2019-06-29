package com.yq.task;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Created by yangqian on 2019/6/29.
 */
public class RunnableTask implements Runnable {

    private long param;

    public RunnableTask() {
    }

    public RunnableTask(long param) {
        this.param = param;
    }

    @Override
    public void run() {
        Config config=new Config();
        config.useSingleServer()
                .setAddress("127.0.0.1:6379");
        //.setPassword(redisConfig.getPassword());

        RedissonClient redisson = Redisson.create(config);
        RAtomicLong atomic = redisson.getAtomicLong("myAtomic");
        atomic.addAndGet(param);
    }

}

package com.yq;


import com.yq.dist.DistLock;
import com.yq.service.MyRedisPubSubListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Simple to Introduction
 * className: MyCommandLineRunner
 *
 * @author EricYang
 * @version 2018/9/2 14:03
 */
@Component
@Slf4j
@Order(value = 4)
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private DistLock distLock;


    @Override
    public void run(String... strings) throws Exception {
        log.info("MyCommandLineRunner初始化 distLock={}", distLock);

        distLock.init();

        RedisClient client = RedisClient.create("redis://127.0.0.1");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new MyRedisPubSubListener());

        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe("topic1");
    }
}



package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.service.MyMessageListener;
import com.yq.service.MyRedisPubSubListener;
import com.yq.service.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@RestController
@RequestMapping("/redis")
@Slf4j
public class RedisController {
    private Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    RedisService redisService;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    RedisConnectionFactory connectionFactory;

    @Autowired
    LettuceConnectionFactory lettuceConnectionFactory;


    @ApiOperation(value = "subscribe, 演示代码没有关闭client和conn", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel", defaultValue = "channel1", value="channel", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/sub/channelsWithMessageListener/{channel}", produces = "application/json;charset=UTF-8")
    public String subscribeChannel(@PathVariable String channel) {
        log.info("connFactory={}, lettuceConnFactory={}", connectionFactory.toString(), lettuceConnectionFactory.toString());
        RedisConnection redisConnection = lettuceConnectionFactory.getConnection();

        redisConnection.subscribe(new MyMessageListener(),channel.getBytes(StandardCharsets.UTF_8) );
        Long count = redisConnection.publish(channel.getBytes(StandardCharsets.UTF_8), "1st".getBytes(StandardCharsets.UTF_8));

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("subscribe", channel);
        jsonObj.put("count", count);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "subscribe With RedisPubSubListener 演示代码没有关闭client和conn", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel", defaultValue = "channel1", value="channel", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/sub/channels/{channel}", produces = "application/json;charset=UTF-8")
    public String subscribeChannelWithRedisPubSubListener(@PathVariable String channel) {
        RedisClient client = RedisClient.create("redis://127.0.0.1");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new MyRedisPubSubListener());

        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe(channel);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("subscribe", channel);
        //connection.close();
        //client.shutdown();
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "publish, 演示代码没有关闭client和conn", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel", defaultValue = "channel1", value="channel", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "msg", defaultValue = "msg1", value="要发送的消息", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/pub/channels/{channel}", produces = "application/json;charset=UTF-8")
    public String pubChannel(@PathVariable String channel, @RequestParam String msg) {
        log.info("connFactory={}, lettuceConnFactory={}", connectionFactory.toString(), lettuceConnectionFactory.toString());
        RedisClient client = RedisClient.create("redis://127.0.0.1");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        RedisFuture<Long>  longRedisFuture = async.publish(channel, msg);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("publish", msg);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "subscribe pattern", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pattern", defaultValue = "__keyevent@[0-1]__:set", value="pattern", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/sub/patternsWithMessageListener/{pattern}", produces = "application/json;charset=UTF-8")
    public String subPatternWithMessageListener(@PathVariable String pattern) {
        log.info("connFactory={}, lettuceConnFactory={}", connectionFactory.toString(), lettuceConnectionFactory.toString());
        RedisConnection redisConnection = lettuceConnectionFactory.getConnection();

        redisConnection.pSubscribe(new MyMessageListener(),pattern.getBytes(StandardCharsets.UTF_8) );

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("psubscribe", pattern);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "subscribe pattern", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pattern", defaultValue = "__keyevent@[0-1]__:expired", value="pattern", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/patterns/{pattern}", produces = "application/json;charset=UTF-8")
    public String subPattern(@PathVariable String pattern) {

        RedisClient client = RedisClient.create("redis://127.0.0.1");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new MyRedisPubSubListener());

        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.psubscribe(pattern);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("psubscribe", pattern);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "创建key value", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", defaultValue = "name1", value = "name1", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "value", defaultValue = "value01", value = "value01", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/keys/{key}", produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String key, @RequestParam String value) {
        redisService.set(key, value);
        value = redisService.get(key);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "简单的多线程读写，只是为了演示，所有直接使用new Thread", notes="read write")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", defaultValue = "5", value = "次数", required = true, dataType = "int", paramType = "path"),
    })
    @PostMapping(value = "/readwrite/{num}", produces = "application/json;charset=UTF-8")
    public String getMultiReadWrite(@PathVariable Integer num) {
        String key = "testKey";
        String hashKey = "hash1";
        redisService.setHash(key, hashKey, "0");

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String value = redisService.getHash(key, hashKey);
                        //logger.info("threadId={}, oldValue={}", Thread.currentThread().getId(), value);
                        if (StringUtils.isEmpty(value)) {
                            value = "0";
                        }
                        Integer oldValue = Integer.valueOf(value);
                        Integer newValue = oldValue + 1;
                        redisService.setHash(key, hashKey, newValue.toString());
                        String againValue = redisService.getHash(key, hashKey);
                        logger.info("threadId={}, oldValue={}, againValue={}", Thread.currentThread().getId(), oldValue, againValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }, "thread-" + i).start();
            //为了确保别的线程修改后新线程能读取，加上间隔时间
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                logger.info("sleep exception", ex);
            }
        };

        String value = redisService.getHash(key, hashKey);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "多线程读写 by pool", notes="read write")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", defaultValue = "8", value = "5", required = true, dataType = "int", paramType = "path"),
    })
    @PostMapping(value = "/readwritePool/{num}", produces = "application/json;charset=UTF-8")
    public String getMultiPoolReadWrite(@PathVariable Integer num) {
        String key = "testKey";
        String hashKey = "hash1";
        redisService.setHash(key, hashKey, "0");

        testInThreadPool(key, hashKey, num);

        String value = redisService.getHash(key, hashKey);


        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    private void testInThreadPool(String key, String hashKey, int num) {
        ThreadPoolExecutor ruleExecutor = new ThreadPoolExecutor(
                2,             /* minimum (core) thread count */
                4,        /* maximum thread count */
                Long.MAX_VALUE, /* timeout */
                TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        Random random = new Random();
        Lock lock = new ReentrantLock();
        for(int i=0; i< num; i++) {
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        String value = redisService.getHash(key, hashKey);
                        //logger.info("threadId={}, oldValue={}", Thread.currentThread().getId(), value);
                        if (StringUtils.isEmpty(value)) {
                            value = "0";
                        }
                        Integer oldValue = Integer.valueOf(value);
                        Integer newValue = oldValue + 1;
                        redisService.setHash(key, hashKey, newValue.toString());
                        String againValue = redisService.getHash(key, hashKey);
                        logger.info("threadId={}, oldValue={}, againValue={}", Thread.currentThread().getId(), oldValue, againValue);
                    } catch (Exception e) {
                        logger.error("Exception. ", e);
                    } finally {
                        lock.unlock();
                    }
                }
            };
            //为了确保别的线程修改后新线程能读取，加上间隔时间
            try {
                int sleepRandom = random.nextInt(200);
                Thread.sleep(sleepRandom);
            } catch (Exception ex) {
                logger.info("sleep exception", ex);
            }

            ruleExecutor.submit(myRunnable);
        }
    }
}
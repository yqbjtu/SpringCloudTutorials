

package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.config.ThreadPool;
import com.yq.dist.DistLock;
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
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@RestController
@RequestMapping("/redis")
@Slf4j
public class RedisMapController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private StringRedisTemplate template;

    @ApiOperation(value = "查看map内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapKey", defaultValue = "mapKey", value="channel", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/maps/{mapKey}", produces = "application/json;charset=UTF-8")
    public String subscribeChannel(@PathVariable String mapKey) {
        RMap map = redisService.getMap(mapKey);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("map", map);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "subscribe With RedisPubSubListener 演示代码没有关闭client和conn", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", defaultValue = "key1", value="key", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "value", defaultValue = "value1", value="value", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/maps", produces = "application/json;charset=UTF-8")
    public String subscribeChannelWithRedisPubSubListener(@RequestParam String key, @RequestParam String value) {
        RMap map = redisService.mapAddEntry(key, value);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("map", map);

        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "publish, 演示代码没有关闭client和conn", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", defaultValue = "key1", value="key1", required = true, dataType = "string", paramType = "query")
    })
    @DeleteMapping(value = "/maps", produces = "application/json;charset=UTF-8")
    public String pubChannel(@RequestParam String key) {
        String value = redisService.mapDelEntry(key);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

}
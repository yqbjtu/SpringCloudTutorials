

package com.yq.controller;

import com.yq.service.RedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@RestController
@RequestMapping("/cache")
public class RedisController {
    private Logger logger = LoggerFactory.getLogger(RedisController.class);

   @Autowired
   RedisService redisService;

    @ApiOperation(value = "设置key", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/keys/{key}", produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String key) {
        String value = redisService.get(key);
        return value;
    }

    @ApiOperation(value = "创建key value", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "value", value = "value01", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/keys/{key}", produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String key, @RequestParam String value) {
        redisService.set(key, value);
        value = redisService.get(key);
        return value;
    }

    @ApiOperation(value = "多线程读写", notes="read write")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "5", required = true, dataType = "int", paramType = "path"),
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
        return value;
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
        return value;
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
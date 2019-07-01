

package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.service.RedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/redis")
@Slf4j
public class RedisExecutorController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private StringRedisTemplate template;

    @ApiOperation(value = "start task")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapKey", defaultValue = "mapKey", value="channel", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/tasks/{mapKey}", produces = "application/json;charset=UTF-8")
    public String subscribeChannel(@PathVariable String mapKey) {
        Future future = redisService.StartRunnable();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("future", future);
        return jsonObj.toJSONString();
    }

}
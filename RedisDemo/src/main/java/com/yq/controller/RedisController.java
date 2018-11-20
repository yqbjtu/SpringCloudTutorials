

package com.yq.controller;

import com.yq.service.RedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/cache")
public class RedisController {
    private Logger logger = LoggerFactory.getLogger(RedisController.class);

   @Autowired
   RedisService redisService;

    @ApiOperation(value = "获取sequence")
    @GetMapping(value = "/sequence", produces = "application/json;charset=UTF-8")
    public long getSequence() {
        long value = redisService.getRedisSequence();
        return value;
    }

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
}
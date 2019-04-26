

package com.yq.controller;

import com.yq.service.RedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

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
    public String getKey(@PathVariable String key) {
        String value = redisService.get(key);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", key);
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "设置key", notes="set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", defaultValue = "abc", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "hkey", value = "hkey", defaultValue = "hkey1",required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/hkeys/{key}", produces = "application/json;charset=UTF-8")
    public String getHash(@PathVariable String key, @RequestParam String hkey) {
        String value = redisService.getHash(key, hkey);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", key);
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "删除key。 不支持匹配", notes="del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1, 放在path中太长会被截断", required = true, dataType = "string", paramType = "query")
    })
    @DeleteMapping(value = "/keys", produces = "application/json;charset=UTF-8")
    public Boolean delKey(@RequestParam String key) {
        Boolean value = redisService.del(key);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("keyDel", value);
        return value;
    }

    @ApiOperation(value = "删除keysPattern。 支持匹配", notes="del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyPattern", value = "name1, 放在path中太长会被截断", required = true, dataType = "string", paramType = "query")
    })
    @DeleteMapping(value = "/keysPattern", produces = "application/json;charset=UTF-8")
    public Integer delByPattern(@RequestParam String keyPattern) {
        int count = redisService.delByPattern(keyPattern);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("keyPatternDel", count);
        return count;
    }

    @ApiOperation(value = "创建key value", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "value", value = "value01", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/keys/{key}", produces = "application/json;charset=UTF-8")
    public String setKey(@PathVariable String key, @RequestParam String value) {

        redisService.set(key, value);
        value = redisService.get(key);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", value);
        return jsonObj.toJSONString();
    }

}
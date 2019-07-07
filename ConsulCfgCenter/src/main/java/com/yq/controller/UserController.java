package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/v1")
@Slf4j
public class UserController {

    private Map<String, User> userMap = new HashMap<>();
    {
        for(int i=0;i < 5; i++) {
            User user = new User();
            user.setId(i + "");
            user.setMail("qq" + i + "@163.com");
            user.setName("Tom" + i );
            user.setRegDate(new Date());
            userMap.put(i+ "",user );
        }
    }

    @ApiOperation(value = "按用户id查询， 参数在path部分", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User getUser(@PathVariable String userId) {
        User user = (User)userMap.get(userId);
        return user;
    }

    @ApiOperation(value = "按用户id查询， 参数在path部分， 故意sleep，让服务间调用耗时较长", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "sleepTimeMillis", defaultValue = "6000", value = "sleepTimeMillis", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/usersWithSleep/{userId}", produces = "application/json;charset=UTF-8")
    public User getUserWithSleep(@PathVariable String userId, @RequestParam Long sleepTimeMillis) {
        User user = (User)userMap.get(userId);
        try {
            Thread.sleep(sleepTimeMillis);
        }
        catch(InterruptedException ex) {
            log.error("sleep exception.", ex);
        }
        return user;
    }

    @ApiOperation(value = "按用户id查询， 只是为了演示参数在query部分", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/users/queryById", produces = "application/json;charset=UTF-8")
    public User getUserByQueryParam(@RequestParam String userId) {
        User user = (User)userMap.get(userId);
        return user;
    }

    @ApiOperation(value = "创建用户， 为演示异常处理，这里直接抛出异常")
    @PostMapping(value = "/users", produces = "application/json;charset=UTF-8")
    public User createUser() throws Exception {
        throw new Exception("create user exception");
    }

    @ApiOperation(value = "查询所有用户")
    @GetMapping(value = "/users", produces = "application/json;charset=UTF-8")
    public Iterable<User> findAllUsers() {
        Collection<User> users = userMap.values();
        return users;
    }

    @ApiOperation(value = "创建key value ,测试produces写错的情况，text/json是不争取的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "value", value = "value01", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/keys/{key}", produces = "text/json;charset=UTF-8")
    public String setKey2(@PathVariable String key, @RequestParam String value) {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", key);
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }
}
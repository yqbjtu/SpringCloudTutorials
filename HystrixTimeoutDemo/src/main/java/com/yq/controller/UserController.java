

package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yq.client.UserClient;
import com.yq.client.UserServiceClient;
import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @ApiOperation(value = "按用户id查询,纯演示为考虑线程安全等", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User getUser(@PathVariable String userId) {
        User user = (User)userMap.get(userId);
        return user;
    }

    @ApiOperation(value = "查询所有用户 纯演示为考虑线程安全等")
    @GetMapping(value = "/users", produces = "application/json;charset=UTF-8")
    public Iterable<User> findAllUsers() {
        Collection<User> users = userMap.values();
        return users;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() { return new RestTemplate(); }

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_NAME = "user-service";

    @Autowired
    UserClient userClient;

    @Autowired
    UserServiceClient userServiceClient;


    @GetMapping(value = "/myusers/{userId}")
    @HystrixCommand(fallbackMethod = "defaultCall")
    //使用断路功能，服务不可用，或者超时会调用defaultCall
    @ApiOperation(value = "按用户id查询 by RestTemplate, 提供了defaultCall作为fallback", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
    })
    public String callService(@PathVariable String userId) {
        log.info("userId={}", userId);
        try {
            String result = restTemplate.getForObject("http://" + SERVICE_NAME + "/v1/user/users/" + userId, String.class);
            JSONObject jsonTemp = new JSONObject();
            jsonTemp.put("result", result);
            return jsonTemp.toString();
        }
        catch (Exception ex ) {
            log.info("userId={}, exception.", userId, ex);
        }

        return "";
    }

    private String defaultCall(String userId) {
        log.info("default userId={}", userId);
        return "service " + SERVICE_NAME + " not available when query '" + userId + "'";
    }

    @ApiOperation(value = "按用户id查询 FeignClient", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping(value = "/feignusers/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByFeign(@PathVariable Integer userId) {
        String result = userClient.getUserDetail(userId.toString());
        JSONObject jsonTemp = new JSONObject();
        jsonTemp.put("result", result);
        return jsonTemp.toString();
    }

    @ApiOperation(value = "按用户id查询 FeignClient, 调用user-server的/usersWithSleep/{userId}加上sleep时间查询", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "sleepTimeMillis", defaultValue = "6000", value = "sleepTimeMillis", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/feign2users/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByFeign2(@PathVariable Integer userId, @RequestParam Long sleepTimeMillis) {
        String result;
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        try {
            result = userServiceClient.getUserDetail(userId.toString(), sleepTimeMillis);
            endTime = System.currentTimeMillis();
            log.info("cost={}, normal", (endTime - startTime));
        }
        catch (Exception ex) {
            endTime = System.currentTimeMillis();
            result = ex.getMessage() + ", "+ (endTime - startTime);
            log.error("cost={}, exception", (endTime - startTime), ex);
        }

        JSONObject jsonTemp = new JSONObject();
        jsonTemp.put("result", result);
        return jsonTemp.toString();
    }
}
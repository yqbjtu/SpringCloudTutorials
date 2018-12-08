

package com.yq.controller;

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

    @ApiOperation(value = "按用户id查询", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User getUser(@PathVariable String userId) {
        User user = (User)userMap.get(userId);
        return user;
    }

    @ApiOperation(value = "查询所有用户")
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

    //private static final String serviceName = "user-service";
    private static final String serviceName = "hystrix-user-service";

    @Autowired
    UserClient userClient;

    @Autowired
    UserServiceClient userServiceClient;


    @GetMapping(value = "/myusers/{userId}")
    @HystrixCommand(fallbackMethod = "defaultCall")
    //使用断路功能，服务不可用，或者超时会调用defaultCall
    @ApiOperation(value = "按用户id查询", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
    })
    public String callService(@PathVariable String userId) {
        log.info("userId={}", userId);
        try {
            String result = restTemplate.getForObject("http://" + serviceName + "/user/users/" + userId, String.class);
            return result;
        }
        catch (Exception ex ) {
            log.info("userId={}, exception.", userId, ex);
        }

        return "";
    }

    private String defaultCall(String userId) {
        log.info("default userId={}", userId);
        return "service " + serviceName + " not available when query '" + userId + "'";
    }

    @ApiOperation(value = "按用户id查询 Feign", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping(value = "/feignusers/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByFeign(@PathVariable Integer userId) {
        String result = userClient.getUserDetail(userId.toString());
        return result;
    }

    @ApiOperation(value = "按用户id查询 Feign2", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping(value = "/feign2users/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByFeign2(@PathVariable Integer userId) {
        String result = userServiceClient.getUserDetail(userId.toString());
        return result;
    }
}
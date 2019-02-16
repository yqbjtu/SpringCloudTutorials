

package com.yq.controller;

import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
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

    @ApiOperation(value = "按用户id查询", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "2", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User getUser(@PathVariable String userId) {
        User user = (User)userMap.get(userId);
        return user;
    }

    @ApiOperation(value = "按用户id修改", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "2", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "user", value = "user", defaultValue = "{\"name\": \"222@163.com\",\n" +
                    "\"mail\": \"WangWu\",\n" +
                    "\"regDate\": 1550305127268\"}", required = true, dataType = "User", paramType = "body")
    })
    @PutMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        user.setId(userId);
        userMap.put(userId, user);
        return user;
    }

    @ApiOperation(value = "查询所有用户")
    @GetMapping(value = "/users", produces = "application/json;charset=UTF-8")
    public Iterable<User> findAllUsers() {
        Collection<User> users = userMap.values();
        return users;
    }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Welcome to reactive world!");
    }

}
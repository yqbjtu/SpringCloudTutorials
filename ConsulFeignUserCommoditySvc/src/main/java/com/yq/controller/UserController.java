

package com.yq.controller;

import com.yq.client.UserServiceClient;
import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/my")
@Slf4j
public class UserController {

    @Autowired
    UserServiceClient userServiceClient;

    @ApiOperation(value = "按用户id查询， 参数在path部分", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/users/{userId}", produces = "application/json;charset=UTF-8")
    public User getUser(@PathVariable String userId) {
        User user = userServiceClient.getUser(userId);
        return user;
    }

    @ApiOperation(value = "按用户id查询， 参数在path部分和query， 另外加上sleep，演示超时效果", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "sleepTimeMillis", defaultValue = "6000", value = "sleepTimeMillis", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/usersWithSleep/{userId}", produces = "application/json;charset=UTF-8")
    public User getUserWithSleep(@PathVariable String userId, @RequestParam Long sleepTimeMillis) {
        User user = userServiceClient.getUserWithSleep(userId, sleepTimeMillis);
        return user;
    }

    @ApiOperation(value = "按用户id查询， 只是为了演示参数在query部分", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", defaultValue = "2", value = "userID", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/users/queryById", produces = "application/json;charset=UTF-8")
    public User getUserByQueryParam(@RequestParam String userId) {
        User user = userServiceClient.getUserByQueryParam(userId);
        return user;
    }

    @ApiOperation(value = "创建用户， 为演示服务间调用异常处理，这里user-service对应的rest会直接抛出异常")
    @PostMapping(value = "/users", produces = "application/json;charset=UTF-8")
    public String createUser() throws Exception {
        String user = userServiceClient.createUser();
        return user;
    }


    @ApiOperation(value = "创建key value 服务间调用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "name1", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "value", value = "value01", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/keys/{key}", produces = "text/json;charset=UTF-8")
    public String setKey2(@PathVariable String key, @RequestParam String value) {
        String keyInfo = userServiceClient.setKey(key, value);
        return keyInfo;
    }

}
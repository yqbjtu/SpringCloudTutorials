package com.yq.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Simple to Introduction
 * className: UserServiceClient
 *
 * @author EricYang
 * @version 2018/10/14 21:06
 */


@FeignClient(value = "user-service", fallback = UserServiceClientFallbackFactory.class)
@Component
public interface UserServiceClient {
    @RequestMapping(value = "/v1/users/{userId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    String getUserDetail(@PathVariable("userId") String userId);

    @RequestMapping(value = "/v1/usersWithSleep/{userId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    String getUserDetail(@PathVariable("userId") String userId, @RequestParam("sleepTimeMillis") Long sleepTimeMillis);
}
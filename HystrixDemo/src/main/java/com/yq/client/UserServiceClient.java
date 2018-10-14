package com.yq.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple to Introduction
 * className: UserServiceClient
 *
 * @author EricYang
 * @version 2018/10/14 21:06
 */

@FeignClient(value = "user-service", fallbackFactory = UserServiceClientFallbackFactory.class)
public interface UserServiceClient {
    @RequestMapping(value = "/user/users/{userId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    String getUserDetail(@PathVariable("userId") String userId);
}
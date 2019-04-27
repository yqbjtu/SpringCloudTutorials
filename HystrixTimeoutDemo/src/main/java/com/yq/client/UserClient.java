package com.yq.client;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple to Introduction
 * className: UserClient
 *
 * @author EricYang
 * @version 2018/10/14 20:24
 */


@FeignClient(value = "user-service", fallbackFactory = UserClientFallbackFactory.class)
@Component
public interface UserClient {

    @RequestMapping(value = "/v1/users/{userId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    String getUserDetail(@PathVariable("userId") String userId);

}

@Component
class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    private static final Logger logger = LoggerFactory.getLogger(UserClientFallbackFactory.class);

    @Override
    public UserClient create(final Throwable throwable) {
        return new UserClient() {
            @Override
            public String getUserDetail(String userId) {
                logger.error("Fallback reason = {}, userId={}", throwable.getMessage(), userId, throwable);

                return "FallbackFactory<UserClient> user-service not available when query '" + userId + "'";
            }
        };
    }


}


package com.yq.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simple to Introduction
 * className: UserServiceClientFallbackFactory
 *
 * @author EricYang
 * @version 2018/10/14 21:07
 */


@Component
@Slf4j
public class UserServiceClientFallbackFactory implements UserServiceClient{
    @Override
    public String getUserDetail(String userId) {
        log.error("Fallback2, userId={}", userId);
        return "user-service not available2 when query '" + userId + "'";
    }
}
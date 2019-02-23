package com.yq.service;

import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple to Introduction
 * className: MyRedisPubSubListener
 *
 * @author EricYang
 * @version 2019/2/22 14:08
 */
@Slf4j
public class MyRedisPubSubListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        log.info("msg1={} on channel {}",  message, channel);
    }

    @Override
    public void message(String pattern, String channel, String message) {
        log.info("msg2={} in channel={}",  message, channel);
    }

    @Override
    public void subscribed(String channel, long count) {
        log.info("sub channel={}, count={}",  channel, count);
    }

    @Override
    public void psubscribed(String pattern, long count) {
        log.info("psub pattern={}, count={}", pattern, count);
    }

    @Override
    public void unsubscribed(String channel, long count) {
        log.info("unsub channel={}, count={}",  channel, count);
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        log.info("punsub channel={}, count={}",  pattern, count);
    }
}

package com.yq.service;

import org.redisson.api.RMap;

import java.util.concurrent.Future;

/**
 * Simple to Introduction
 * className: RedisService
 *
 * @author EricYang
 * @version 2018/8/4 23:00
 */
public interface RedisService {
    Future StartRunnable();
    RMap getMap(String key);
    RMap mapAddEntry(String key, String value) ;
    String mapDelEntry(String key);
    String get(String key);
    void set(String key, String value);
    String getHash(String key, String hashKey);
    void setHash(String key, String hashKey, String value);
}


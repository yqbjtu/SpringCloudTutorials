package com.yq.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 */
@Component
@Data
@Slf4j
@Order(1)
public class ZkConfig {

    @Value("${zkServers}")
    private String zkServers;

    @PostConstruct
    public void postConstruct(){
        log.info("zkServers={}", zkServers);
    }
}

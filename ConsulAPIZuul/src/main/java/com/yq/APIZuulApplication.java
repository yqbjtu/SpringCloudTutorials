package com.yq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableHystrix
@EnableHystrixDashboard
public class APIZuulApplication  {
    private static final Logger logger = LoggerFactory.getLogger(APIZuulApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(APIZuulApplication.class, args);
        logger.info("Start APIZuulApplication Done");
    }



}



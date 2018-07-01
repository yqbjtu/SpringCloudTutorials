package com.yq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;


@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class APIZuulApplication extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(APIZuulApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(APIZuulApplication.class, args);
        logger.info(" Start APIZuulApplication Done");
    }



}



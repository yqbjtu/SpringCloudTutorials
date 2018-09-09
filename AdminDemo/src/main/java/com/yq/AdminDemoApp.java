package com.yq;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;



@Configuration
@EnableAutoConfiguration
@EnableAdminServer
@EnableDiscoveryClient

public class AdminDemoApp {
    private static final Logger log = LoggerFactory.getLogger(AdminDemoApp.class);

    public static void main(String[] args) {
        SpringApplication.run(AdminDemoApp.class, args);
    }

}

package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSocketApplication {
    private static final Logger log = LoggerFactory.getLogger(WebSocketApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(WebSocketApplication.class, args);
        log.info("Spring Boot start done!");
    }
}

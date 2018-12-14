package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebSocket2Application {
    private static final Logger log = LoggerFactory.getLogger(WebSocket2Application.class);

    public static void main(String[] args) {

        SpringApplication.run(WebSocket2Application.class, args);
        log.info("Spring Boot start done!");
    }
}
